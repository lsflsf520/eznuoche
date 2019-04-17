package com.xyz.eznuoche.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.xyz.eznuoche.entity.UserCar;
import com.xyz.eznuoche.service.UserCarService;
import com.xyz.tools.common.utils.ThreadUtil;

@Controller
@RequestMapping("/")
public class IndexController {

	@Resource
	private UserCarService userCarService;
	
	@RequestMapping("/toIndex")
	private ModelAndView toIndex() {
		List<UserCar> myCars = userCarService.loadMyCars(ThreadUtil.getUidInt());
		
		ModelAndView mav = new ModelAndView("eznuoche/index");
		mav.addObject("myCars", myCars);
		
		return mav;
	}
	
}
