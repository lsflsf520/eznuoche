package com.xyz.eznuoche.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xyz.eznuoche.dto.Sms;
import com.xyz.eznuoche.entity.Balance;
import com.xyz.eznuoche.entity.OfflineUserCar;
import com.xyz.eznuoche.entity.RegUser;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.eznuoche.entity.UserCar;
import com.xyz.eznuoche.service.IBalanceService;
import com.xyz.eznuoche.service.OfflineUserCarService;
import com.xyz.eznuoche.service.RegUserService;
import com.xyz.eznuoche.service.SmsService;
import com.xyz.eznuoche.service.ThirdUserService;
import com.xyz.eznuoche.service.UserCarService;
import com.xyz.eznuoche.utils.MsgUtil;
import com.xyz.tools.cache.constant.DefaultJedisKeyNS;
import com.xyz.tools.cache.redis.RedisDistLock;
import com.xyz.tools.cache.redis.ShardJedisTool;
import com.xyz.tools.common.bean.ResultModel;
import com.xyz.tools.common.constant.CommonStatus;
import com.xyz.tools.common.constant.GlobalConstant;
import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.common.utils.DateUtil;
import com.xyz.tools.common.utils.EncryptTools;
import com.xyz.tools.common.utils.JsonUtil;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.RegexUtil;
import com.xyz.tools.common.utils.StringUtil;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.web.util.LogonUtil;
import com.xyz.tools.web.util.LogonUtil.SessionUser;
import com.xyz.tools.web.util.WebUtils;
import com.xyz.tools.web.util.WxTool;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;

@Controller
public class IndexController {

	@Resource
	private RegUserService regUserService;
	
	@Resource
	private ThirdUserService thirdUserService;
	
	@Resource
	private UserCarService userCarService;
	
	@Resource
	private OfflineUserCarService offlineUserCarService;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private IBalanceService balanceService;
	
	@RequestMapping("/toIndex")
	public ModelAndView toIndex() {
		ModelAndView mav = new ModelAndView("eznuoche/index");
		
		SessionUser suser = null;
		try{
			suser = LogonUtil.getSessionUser();
			List<UserCar> myCars = userCarService.loadMyCars(ThreadUtil.getUidInt());
			mav.addObject("myCars", myCars);
			mav.addObject("hasLogon", true);
			
			Balance balance = balanceService.findById(suser.getUidInt());
			mav.addObject("balance", balance);
		} catch (BaseRuntimeException e) {
			LogUtils.warn(e.getMessage());
		}
		
		if(ThreadUtil.isWxClient()){
			if(suser == null || suser.needBindPhone()) {
				mav.addObject("needBindPhone", true);
				return mav;
			}
			String currUrl = ThreadUtil.getCurrUrl();
			try {
				WxJsapiSignature signature = WxTool.getWxMpService().createJsapiSignature(currUrl);
				mav.addObject("signature", signature);
				
				String shareUrl = currUrl;
				if(suser != null && StringUtils.isNotBlank(suser.getMyCode())) {
					String myCode = suser.getMyCode();
					String linkUrl = WebUtils.addParam(currUrl, "fcode", myCode);
					shareUrl = linkUrl;
				} 
				mav.addObject("linkUrl", shareUrl);
				LogUtils.info("signature:%s,shareUrl:%s", JsonUtil.create().toJson(signature), shareUrl);
			} catch (WxErrorException e) {
				LogUtils.warn("get wx jsapi signature error", e);
			}
		}
		
		return mav;
	}
	
	
	@PostMapping("bindUser")
	@ResponseBody
	public ResultModel bindUser(HttpServletRequest request, HttpServletResponse response, String plateNo) {
		String phone = LogonUtil.getMobileOrEmail(request);
		if(!RegexUtil.isPhone(phone)){
			return new ResultModel("ILLEGAL_STATE", "数据状态不正常，请重试");
		}
		
		ThirdUser channelUser = null;
		SessionUser suser = null;
		try{
			suser = LogonUtil.getSessionUser();
		} catch(Exception e) {
			//do nothing
		}
		if(suser != null) {
			if(!suser.needBindPhone()) {
				return new ResultModel("NOT_NEED_BIND", "当前无需绑定账号");
			}
			
			if(StringUtils.isBlank(suser.getChannelUid())) {
				return new ResultModel("ILLEGAL_STATE", "当前用户数据状态异常");
			}
			
			channelUser = thirdUserService.loadByChannelUid(suser.getChannelUid());
			if(channelUser == null) {
				LogUtils.warn("not found channel user for channelUid %s", suser.getChannelUid());
				return new ResultModel("NOT_EXIST", "当前第三方用户不存在");
			}
		}
		
		RegUser dbData = regUserService.loadByEncyptPhone(EncryptTools.phoneEncypt(phone)); 
		if(dbData == null){
			int inviteUid = 0;
			String inviteUidStr = inviteUid + "";
			if(channelUser != null) {
				inviteUid = ThreadUtil.parseDirectInviteUid(channelUser.getInviteUid());
				inviteUidStr = StringUtils.isBlank(channelUser.getInviteUid()) ? "0" : channelUser.getInviteUid();
			}
			if(inviteUid <= 0) {
				RegUser inviter = null;
				String inviteCode = ThreadUtil.getInviteCode();
				if(StringUtils.isNotBlank(inviteCode)) {
					inviter = regUserService.loadByMyCode(inviteCode.trim());
					if(inviter == null) {
						LogUtils.warn("not found inviter with inviteCode %s", inviteCode);
					} 
				}
				
				inviteUidStr = RegUserService.buildInviteUid(inviter);
				inviteUid = inviter.getId();
			}
			
			dbData = regUserService.doReg4ChannelUser(phone, channelUser == null || StringUtils.isBlank(channelUser.getNickName()) 
					                     ? StringUtil.stringHide(phone) : channelUser.getNickName(), inviteUidStr);
			if(inviteUid > 0){
				balanceService.addBalace4Inviter(inviteUid);
			}
		} 
		
		if(channelUser != null) {
			channelUser.setUid(dbData.getId());
			thirdUserService.update(channelUser);
		}
		
		suser = regUserService.convertSessionUser(dbData);
		regUserService.doLogin(request, response, suser, false);
		
		userCarService.saveCarNum(suser.getUidInt(), plateNo);
		
		return new ResultModel(true); 
	}

	@PostMapping("addPlateNo")
	@ResponseBody
	public ResultModel addPlateNo(String plateNo) {
		if(StringUtils.isBlank(plateNo)) {
			return new ResultModel("ILLEGAL_PARAM", "车牌号不能为空");
		}

		userCarService.saveCarNum(ThreadUtil.getUidInt(), plateNo);
		
		return new ResultModel(true);
	}
	
	@GetMapping("delPlateNo")
	@ResponseBody
	public ResultModel delPlateNo(int id) {
		UserCar dbData = userCarService.findById(id);
		if(dbData != null && CommonStatus.Normal.equals(dbData.getState()) && ThreadUtil.getUidInt().equals(dbData.getUid())){
			userCarService.softDel(id);
			
			return new ResultModel(true);
		}
		
		return new ResultModel("ILLEGAL_STATE", "数据状态不正常");
	}
	
	@RequestMapping("offline")
	public ModelAndView offline(){
		int currNum = offlineUserCarService.loadNum();
		return new ModelAndView("eznuoche/offline_uc").addObject("currNum", currNum);
	}
	
	@PostMapping("offlinePlateNo")
	@ResponseBody
	public ResultModel offlinePlateNo(String plateNo, String phone, String carBrand,
			                         String province, String city, String county, String detailAddr) {
		if(StringUtils.isBlank(plateNo)) {
			return new ResultModel("ILLEGAL_PARAM", "车牌号不能为空");
		}
		if(!RegexUtil.isPhone(phone)){
			return new ResultModel("ILLEGAL_PARAM", "手机号不正确");
		}
		
		OfflineUserCar dbData = offlineUserCarService.loadByPlateNo(plateNo);
		if(dbData != null) {
			return new ResultModel("ALREADY_EXIST", "该车牌号已录入");
		}
		
		OfflineUserCar updata = new OfflineUserCar();
		updata.setPlateNo(plateNo.toUpperCase());
		updata.setPhone(EncryptTools.phoneEncypt(phone));
		updata.setCarBrand(carBrand);
		updata.setProvince(province);
		updata.setCity(city);
		updata.setCounty(county);
		updata.setDetailAddr(detailAddr);
		updata.setIp(ThreadUtil.getClientIP());
		updata.setCreateTime(new Date());
		
		offlineUserCarService.insert(updata);
		
		return new ResultModel(true);
	}
	
	@RequestMapping("/plateNo/exists")
	@ResponseBody
	public ResultModel exists(String plateNo) {
		OfflineUserCar dbData = offlineUserCarService.loadByPlateNo(plateNo);
		if(dbData != null) {
			return new ResultModel("ALREADY_EXIST", "该车牌已录入");
		}
		UserCar ucar = userCarService.loadByPlateNo(plateNo);
		if(ucar != null) {
			return new ResultModel("ALREADY_EXIST", "该车牌已注册");
		}
		return new ResultModel(true);
	}
	
	@RequestMapping("/{msgType}/notify")
	@ResponseBody
	public ResultModel wxNotify(@PathVariable MsgType msgType, String plateNo) {
		if(StringUtils.isBlank(plateNo) || msgType == null) {
			return new ResultModel("ILLEGAL_PARAM", "参数错误");
		}
		Balance balance = balanceService.findById(ThreadUtil.getUidInt());
		if(balance == null) {
			LogUtils.warn("balance is not exist for uid %d", ThreadUtil.getUidInt());
			return new ResultModel("NOT_ENOUGH", "您所剩通知次数不足");
		}
		RegUser targetUser = new RegUser();
		UserCar targetData = userCarService.loadByPlateNo(plateNo);
		if(targetData == null ){
			if(MsgType.WX.equals(msgType)) {
				return new ResultModel("NOT_EXIST", "先分享本页面邀请该车主进行登记，您还可以获得5次免费短信通知服务哦！");
			}
			OfflineUserCar ouc = offlineUserCarService.loadByPlateNo(plateNo);
			if(ouc == null){
				return new ResultModel("NOT_EXIST", "先分享本页面邀请该车主进行登记，您还可以获得5次免费短信通知服务哦！");
			}
			targetUser.setPhone(ouc.getPhone());
			targetUser.setState(CommonStatus.Normal);
		} else {
			if(ThreadUtil.getUidInt().equals(targetData.getUid())){
				return new ResultModel("PLATE_OWNER", "该车牌号属于您自己，请确认车牌号是否填写正确");
			}
			targetUser = regUserService.findById(targetData.getUid());
			if(targetUser == null || !targetUser.isNormal()) {
				return new ResultModel("ILLEGAL_STATE", "数据状态异常，请联系客服或稍后重试！");
			}
		}
		if(MsgType.WX.equals(msgType)) {
			/*if(balance.getWxCnt() <= 0){
				return new ResultModel("NOT_ENOUGH_WX", "您所剩微信通知次数不足");
			}*/
			try{
				boolean result = RedisDistLock.trylock("notify_wx", targetUser.getPhone());
				if(!result) {
					return new ResultModel("REPEAT_OPER", "正在通知该用户，请勿重复操作！");
				}
				String hasSendIn5Min = ShardJedisTool.hget(DefaultJedisKeyNS.mb_vc, targetUser.getPhone(), "wx");
				if(StringUtils.isNotBlank(hasSendIn5Min)) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				String sendNumIn24Hour = ShardJedisTool.get(DefaultJedisKeyNS.ei, "wx" + targetUser.getPhone());
				if(RegexUtil.isInt(sendNumIn24Hour) && Integer.valueOf(sendNumIn24Hour) > 5) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				ThirdUser targetWxUser = thirdUserService.loadByUid(targetUser.getId());
				if(targetWxUser == null || StringUtils.isBlank(targetWxUser.getThirdUid())) {
					return new ResultModel("ILLEGAL_STATE", "这个小伙伴还没有微信授权认证，请先使用短信通知功能吧");
				}
				/*
				 * {{first.DATA}}
					号牌号码：{{keyword1.DATA}}
					发起时间：{{keyword2.DATA}}
					对方留言：{{keyword3.DATA}}
					{{remark.DATA}}
				 */
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("first", "您好，您收到一个挪车请求。");
				paramMap.put("keyword1", targetData.getPlateNo());
				paramMap.put("keyword2", DateUtil.getCurrentDateTimeStr());
				paramMap.put("keyword3", "您的爱车挡住我的路了，麻烦速来挪一下，感谢！");
				paramMap.put("remark", "关注公众号“北京朝朝盈”，使用EZ挪车，保护隐私，方便他人。");
			    boolean notifyResult = WxTool.sendWxTmplMsg(targetWxUser.getThirdUid(), "r6hfyXxN2sBvOg7I6PzHkRm4V53Jz1M8NtOrWkBPgH0", null, paramMap);
				if(!notifyResult) {
                    return new ResultModel("NOTIFY_ERR", "对不起，微信通知失败，请改用其他通知方式！");					
				}
				ShardJedisTool.hset(DefaultJedisKeyNS.mb_vc, targetUser.getPhone(), "wx", 1);
				ShardJedisTool.incr(DefaultJedisKeyNS.ei, "wx" + targetUser.getPhone());
			} finally {
				RedisDistLock.release("notify_wx", targetUser.getId());
			}
		} else if (MsgType.SMS.equals(msgType)) {
			if(balance.getSmsCnt() <= 0){
				return new ResultModel("NOT_ENOUGH_SMS", "您所剩短信通知次数不足");
			}
			try{
				boolean result = RedisDistLock.trylock("notify_sms", targetUser.getPhone());
				if(!result) {
					return new ResultModel("REPEAT_OPER", "正在通知该用户，请勿重复操作！");
				}
				String hasSendIn5Min = ShardJedisTool.hget(DefaultJedisKeyNS.mb_vc, targetUser.getPhone(), "sms");
				if(StringUtils.isNotBlank(hasSendIn5Min)) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				String sendNumIn24Hour = ShardJedisTool.get(DefaultJedisKeyNS.ei, "sms" + targetUser.getPhone());
				if(RegexUtil.isInt(sendNumIn24Hour) && Integer.valueOf(sendNumIn24Hour) > 5) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				Sms sms = new Sms();
				sms.setAppId(MsgUtil.APP_ID);
				sms.setPhone(targetUser.getDecryptPhone());
				sms.setTmplId(BaseConfig.getValue("notify.sms.tmplid", "426905"));
				sms.setParams(Arrays.asList(plateNo));
				sms.setModule(GlobalConstant.PROJECT_NAME);
				ResultModel resultModel = smsService.send(sms);
				if(!resultModel.isSuccess()) {
					return new ResultModel("NOTIFY_ERR", "对不起，短信通知失败，请改用其他通知方式！");	
				}
				balanceService.minusBalance(targetUser.getId(), msgType, ThreadUtil.getUidInt());
				ShardJedisTool.hset(DefaultJedisKeyNS.mb_vc, targetUser.getPhone(), "sms", 1);
				ShardJedisTool.incr(DefaultJedisKeyNS.ei, "sms" + targetUser.getPhone());
			} finally {
				RedisDistLock.release("notify_sms", targetUser.getPhone());
			}
		} else if (MsgType.TEL.equals(msgType)) {
			if(balance.getTelCnt() <= 0){
				return new ResultModel("NOT_ENOUGH_TEL", "您所剩语音通知次数不足");
			}
			return new ResultModel("NOT_SUPPORT", "本功能正在努力开发中，敬请期待");
		} else {
			return new ResultModel("NOT_SUPPORT", "不支持该通知方式");
		}
		
		return new ResultModel(true);
	}
	
	
}
