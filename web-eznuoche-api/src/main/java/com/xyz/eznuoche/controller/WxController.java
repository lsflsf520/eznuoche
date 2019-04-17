package com.xyz.eznuoche.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.xyz.eznuoche.entity.RegUser;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.eznuoche.service.RegUserService;
import com.xyz.eznuoche.service.ThirdUserService;
import com.xyz.tools.cache.redis.RedisDistLock;
import com.xyz.tools.common.constant.Sex;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.web.util.LogonUtil;
import com.xyz.tools.web.util.LogonUtil.SessionUser;
import com.xyz.tools.web.util.WebUtils;
import com.xyz.tools.web.util.WxTool;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

@Controller
@RequestMapping("wx")
public class WxController {
	
	@Resource
	private RegUserService regUserService;
	
	@Resource
	private ThirdUserService thirdUserService;
	
	@RequestMapping("doLogon")
	public ModelAndView doLogon(HttpServletRequest request, HttpServletResponse response, String code){
		String parsedUrl = WebUtils.parseRedirectUrl(request, true, "/user/port", "/wx/doLogon");
		String redirectUrl = parsedUrl;
		if(StringUtils.isNotBlank(redirectUrl) && redirectUrl.contains("referer=")){
			String parts[] = redirectUrl.split("referer=");
			if(parts.length >= 2){
				String referer = parts[1].split("&")[0];
				referer = WebUtils.urlDecode(referer);
				redirectUrl =  WebUtils.urlDecode(referer);
			}
		}
		LogUtils.info("wxlogon code:%s, redirect:%s, parsedUrl:%s", code, redirectUrl, parsedUrl);
		if(!LogonUtil.hasLogon()){
			WxMpUser wxUser = null;
			try {
				WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxTool.getWxMpService().oauth2getAccessToken(code);
				
				wxUser = WxTool.getWxMpService().oauth2getUserInfo(wxMpOAuth2AccessToken, null);
				
				RedisDistLock.trylock("wxuser_login", wxUser.getOpenId());
				
				RegUser inviter = null;
				String inviteCode = ThreadUtil.getInviteCode();
				if(StringUtils.isNotBlank(inviteCode)) {
					inviter = regUserService.loadByMyCode(inviteCode.trim());
					if(inviter == null) {
						LogUtils.warn("not found inviter with inviteCode %s", inviteCode);
					} 
				}
				
				String inviteUid = RegUserService.buildInviteUid(inviter);
				
				ThirdUser channelUser = thirdUserService.loadByChannelUid(wxUser.getOpenId());
				if(channelUser == null) {
					channelUser = new ThirdUser();
					channelUser.setNickName(wxUser.getNickname());
					channelUser.setHeadImg(wxUser.getHeadImgUrl());
					channelUser.setUid(0); //默认没有绑定主账号
					channelUser.setSex(wxUser.getSexId() == null ? Sex.U : (wxUser.getSexId() == 1 ? Sex.M : Sex.F));
					channelUser.setThirdUid(wxUser.getOpenId());
					channelUser.setInviteUid(inviteUid);
					
					thirdUserService.insertReturnPK(channelUser);
				} else {
					boolean hasChange = false;
					if(StringUtils.isNotBlank(wxUser.getHeadImgUrl()) && !wxUser.getHeadImgUrl().equals(channelUser.getHeadImg())){
						channelUser.setHeadImg(wxUser.getHeadImgUrl());
						
						hasChange = true;
					}
					if(StringUtils.isNotBlank(wxUser.getNickname()) && !wxUser.getNickname().equals(channelUser.getNickName())){
						channelUser.setNickName(wxUser.getNickname());
						
						hasChange = true;
					}
					
					if(hasChange) {
						thirdUserService.update(channelUser);
					}
				}
				
				SessionUser suser = null;
				if(channelUser.getUid() != null && channelUser.getUid() > 0) {
					RegUser dbData = regUserService.findById(channelUser.getUid());
					if(dbData != null && dbData.isNormal()) {
						suser = regUserService.convertSessionUser(dbData);
					}
				} 
				
				if(suser == null) {
					suser = RegUserService.convertWxChannelUser(channelUser);
				}
				
				regUserService.doLogin(request, response, suser, true);
				
			} catch (WxErrorException e) {
				LogUtils.warn(e.getMessage(), e);
			} finally {
				if(wxUser != null && StringUtils.isNotBlank(wxUser.getOpenId())){
					RedisDistLock.release("wxuser_login", wxUser.getOpenId());
				}
			}
			
		}
		
		return new ModelAndView("redirect:"+ redirectUrl);
	}

}
