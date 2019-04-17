package com.xyz.eznuoche.aop;


import javax.annotation.Resource;

import com.xyz.eznuoche.service.RegUserService;
import com.xyz.tools.common.utils.EncryptTools;
import com.xyz.tools.web.aop.PhoneExistCheckInterceptor.PhoneExistChecker;

public class UserExistChecker implements PhoneExistChecker {
	
	@Resource
	private RegUserService regUserService;

	@Override
	public boolean isExist(String phone) {
		String encypt = EncryptTools.phoneEncypt(phone);
		return regUserService.loadByEncyptPhone(encypt) != null;
	}

}
