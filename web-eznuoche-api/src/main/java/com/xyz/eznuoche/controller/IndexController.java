package com.xyz.eznuoche.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xyz.eznuoche.entity.RegUser;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.eznuoche.entity.UserCar;
import com.xyz.eznuoche.service.RegUserService;
import com.xyz.eznuoche.service.ThirdUserService;
import com.xyz.eznuoche.service.UserCarService;
import com.xyz.tools.common.bean.ResultModel;
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
	
	@RequestMapping("/toIndex")
	public ModelAndView toIndex() {
		ModelAndView mav = new ModelAndView("eznuoche/index");
		if(ThreadUtil.isWxClient()){
			SessionUser suser = LogonUtil.getSessionUser();
			if(suser == null || suser.needBindPhone()) {
				mav.addObject("needBindPhone", true);
				return mav;
			}
		}
		List<UserCar> myCars = userCarService.loadMyCars(ThreadUtil.getUidInt());
		
		mav.addObject("myCars", myCars);
		
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
		SessionUser suser = ThreadUtil.getCurrUser();
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
			int inviteUid = ThreadUtil.parseDirectInviteUid(channelUser.getInviteUid());
			String inviteUidStr = channelUser == null ? "0" : channelUser.getInviteUid();
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
			}
			
			dbData = regUserService.doReg4ChannelUser(phone, channelUser == null || StringUtils.isBlank(channelUser.getNickName()) 
					                     ? StringUtil.stringHide(phone) : channelUser.getNickName(), inviteUidStr);
		} 
		
		channelUser.setUid(dbData.getId());
		thirdUserService.update(channelUser);
		
		suser = regUserService.convertSessionUser(dbData);
		regUserService.doLogin(request, response, suser, false);
		
		userCarService.saveCarNum(suser.getUidInt(), plateNo);
		
		return new ResultModel(true); 
	}

	@PostMapping("addPlateNo")
	@ResponseBody
	public ResultModel addPlateNo(String plateNo) {
		userCarService.saveCarNum(ThreadUtil.getUidInt(), plateNo);
		
		return new ResultModel(true);
	}
}
