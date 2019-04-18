package com.xyz.eznuoche.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xyz.eznuoche.entity.UserCar;
import com.xyz.eznuoche.service.UserCarService;
import com.xyz.tools.common.bean.ResultModel;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.web.util.LogonUtil;
import com.xyz.tools.web.util.LogonUtil.SessionUser;

@Controller
@RequestMapping("/")
public class IndexController {

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
	
	@RequestMapping("/{msgType}/notify")
	@ResponseBody
	public ResultModel wxNotify(@PathVariable String msgType, String carNum) {
		
		
		return new ResultModel(true);
	}
	
	
	
}
