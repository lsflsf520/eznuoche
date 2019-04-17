package com.xyz.eznuoche.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xyz.tools.common.constant.GlobalConstant;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.RandomUtil;
import com.xyz.tools.web.aop.AbstractInterceptor;
import com.xyz.tools.web.util.WebUtils;
import com.xyz.tools.web.util.WxTool;

public class WxLoginInterceptor extends AbstractInterceptor {

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			String requestUri) throws Exception {
		try{
			String currUrl = WebUtils.getCurrParamUrl(request);
			String logonUrl = GlobalConstant.PROJECT_H5_DOMAIN + "/wx/doLogon.do";
			logonUrl = WebUtils.addParam(logonUrl, "referer", WebUtils.urlEncode(currUrl));
			String authUrl = WxTool.getWxMpService().oauth2buildAuthorizationUrl(logonUrl, "snsapi_userinfo", RandomUtil.randomAlphaNumCode(6));
			LogUtils.info("authUrl: %s", authUrl);
			response.sendRedirect(authUrl);
			return false;
		} catch (Exception e){
			LogUtils.warn("errorMsg: %s", e.getMessage());
		}
		
		/*if(ThreadUtil.isWxClient() && !LogonUtil.hasLogon()) {
			throw new BaseRuntimeException("NEED_WX_AUTH", "需要微信授权");
		}*/
		
		return true;
	}
	
	

}
