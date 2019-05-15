package com.xyz.eznuoche.controller;

import java.util.Arrays;
import java.util.List;

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
import com.xyz.eznuoche.entity.RegUser;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.eznuoche.entity.UserCar;
import com.xyz.eznuoche.service.IBalanceService;
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
import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.common.utils.EncryptTools;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.RegexUtil;
import com.xyz.tools.common.utils.StringUtil;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.web.util.LogonUtil;
import com.xyz.tools.web.util.LogonUtil.SessionUser;

@Controller
public class IndexController {

	@Resource
	private RegUserService regUserService;
	
	@Resource
	private ThirdUserService thirdUserService;
	
	@Resource
	private UserCarService userCarService;
	
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
	
	@RequestMapping("/{msgType}/notify")
	@ResponseBody
	public ResultModel wxNotify(@PathVariable MsgType msgType, String plateNo) {
		if(StringUtils.isBlank(plateNo) || msgType == null) {
			return new ResultModel("ILLEGAL_PARAM", "参数错误");
		}
		UserCar targetData = userCarService.loadByPlateNo(plateNo);
		if(targetData == null){
			return new ResultModel("NOT_EXIST", "点击右上角的 ... 按钮先邀请该车主进行登记，您还可以获得5次免费短信通知服务哦！");
		}
		RegUser targetUser = regUserService.findById(targetData.getUid());
		if(targetUser == null || !targetUser.isNormal()) {
			return new ResultModel("ILLEGAL_STATE", "数据状态异常，请联系客服或稍后重试！");
		}
		Balance balance = balanceService.findById(ThreadUtil.getUidInt());
		if(balance == null) {
			LogUtils.warn("balance is not exist for uid %d", targetUser.getId());
			return new ResultModel("NOT_ENOUGH", "您所剩通知次数不足");
		}
		if(MsgType.WX.equals(msgType)) {
			/*if(balance.getWxCnt() <= 0){
				return new ResultModel("NOT_ENOUGH_WX", "您所剩微信通知次数不足");
			}*/
			try{
				boolean result = RedisDistLock.trylock("notify_wx", targetUser.getId());
				if(!result) {
					return new ResultModel("REPEAT_OPER", "正在通知该用户，请勿重复操作！");
				}
				String hasSendIn5Min = ShardJedisTool.hget(DefaultJedisKeyNS.mb_vc, targetUser.getId(), "wx");
				if(StringUtils.isNotBlank(hasSendIn5Min)) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				String sendNumIn24Hour = ShardJedisTool.get(DefaultJedisKeyNS.ei, "wx" + targetUser.getId());
				if(RegexUtil.isInt(sendNumIn24Hour) && Integer.valueOf(sendNumIn24Hour) > 5) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				ThirdUser targetWxUser = thirdUserService.loadByUid(targetUser.getId());
				if(targetWxUser == null || StringUtils.isBlank(targetWxUser.getThirdUid())) {
					return new ResultModel("ILLEGAL_STATE", "这个小伙伴还没有微信授权认证，请先使用短信通知功能吧");
				}
//			WxTool.sendWxTmplMsg(targetWxUser.getThirdUid(), templateId, url, paramMap);
				
				ShardJedisTool.hset(DefaultJedisKeyNS.mb_vc, targetUser.getId(), "wx", 1);
				ShardJedisTool.incr(DefaultJedisKeyNS.ei, "wx" + targetUser.getId());
			} finally {
				RedisDistLock.release("notify_wx", targetUser.getId());
			}
		} else if (MsgType.SMS.equals(msgType)) {
			if(balance.getSmsCnt() <= 0){
				return new ResultModel("NOT_ENOUGH_SMS", "您所剩短信通知次数不足");
			}
			try{
				boolean result = RedisDistLock.trylock("notify_sms", targetUser.getId());
				if(!result) {
					return new ResultModel("REPEAT_OPER", "正在通知该用户，请勿重复操作！");
				}
				String hasSendIn5Min = ShardJedisTool.hget(DefaultJedisKeyNS.mb_vc, targetUser.getId(), "sms");
				if(StringUtils.isNotBlank(hasSendIn5Min)) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				String sendNumIn24Hour = ShardJedisTool.get(DefaultJedisKeyNS.ei, "sms" + targetUser.getId());
				if(RegexUtil.isInt(sendNumIn24Hour) && Integer.valueOf(sendNumIn24Hour) > 5) {
					return new ResultModel("FREQUENCY_SEND", "发送太频繁，请稍后重试！");
				}
				Sms sms = new Sms();
				sms.setAppId(MsgUtil.APP_ID);
				sms.setPhone(targetUser.getDecryptPhone());
				sms.setTmplId(BaseConfig.getValue("notify.sms.tmplid", "428109"));
				sms.setParams(Arrays.asList(plateNo));
				ResultModel resultModel = smsService.send(sms);
				if(resultModel.isSuccess()) {
					balanceService.minusBalance(targetUser.getId(), msgType, ThreadUtil.getUidInt());
				}
				ShardJedisTool.hset(DefaultJedisKeyNS.mb_vc, targetUser.getId(), "sms", 1);
				ShardJedisTool.incr(DefaultJedisKeyNS.ei, "sms" + targetUser.getId());
			} finally {
				RedisDistLock.release("notify_sms", targetUser.getId());
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
