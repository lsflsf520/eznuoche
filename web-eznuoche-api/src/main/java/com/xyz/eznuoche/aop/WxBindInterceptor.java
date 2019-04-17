package com.xyz.eznuoche.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.web.aop.AbstractInterceptor;
import com.xyz.tools.web.util.LogonUtil.SessionUser;

public class WxBindInterceptor extends AbstractInterceptor {

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			String requestUri) throws Exception {
		if(ThreadUtil.isWxClient()){
			SessionUser suser = ThreadUtil.getCurrUser();
			if(suser == null || suser.needBindPhone()) {
				throw new BaseRuntimeException("NEED_BIND_ACC", "需要绑定账号");
			}
		}
		return true;
	}

}
