package com.xyz.eznuoche.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.xyz.eznuoche.entity.ServAppoint;
import com.xyz.eznuoche.service.ServAppointService;
import com.xyz.tools.common.bean.ResultModel;
import com.xyz.tools.common.constant.CheckState;
import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.RegexUtil;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.db.bean.PageData;
import com.xyz.tools.web.util.LogonUtil;
import com.xyz.tools.web.util.LogonUtil.SessionUser;

@Controller
@RequestMapping("servAppoint")
public class ServAppointController {

	@Resource
	private ServAppointService servAppointService;
	
	@RequestMapping("toPage")
	public ModelAndView toPage() {
		ModelAndView mav = new ModelAndView("serv_appoint/page");
		SessionUser suser = null;
		try{
			suser = LogonUtil.getSessionUser();
			PageData<ServAppoint> dataPage = servAppointService.loadMyServAppoints(suser.getUidInt(), 1);
			mav.addObject("dataPage", dataPage);
		} catch (BaseRuntimeException e) {
			LogUtils.warn(e.getMessage());
		}
		
		return mav;
	}
	
	@RequestMapping("loadMyServAppoints")
	@ResponseBody
	public ResultModel loadMyServAppointsByPage(int currPage) {
		PageData<ServAppoint> dataPage = servAppointService.loadMyServAppoints(ThreadUtil.getUidInt(), currPage);
	
	    return new ResultModel(dataPage);
	}
	
	@RequestMapping("add")
	@ResponseBody
	public ResultModel addServAppoint(ServAppoint servAppoint) {
		if(RegexUtil.isPhone(servAppoint.getPhone())) {
			return new ResultModel("ILLEGAL_PHONE", "手机号格式不正确");
		}
		if(servAppoint.getAppointTime() == null) {
			return new ResultModel("ILLEGAL_APTIME", "预约时间不能为空");
		}
		servAppoint.setUid(ThreadUtil.getUidInt());
		
		servAppointService.doSave(servAppoint);
		
		return new ResultModel(true);
	}
	
	@RequestMapping("cancel")
	@ResponseBody
	public ResultModel cancel(int id) {
		ServAppoint dbData = servAppointService.findById(id);
		if(dbData == null || !CheckState.Checking.equals(dbData.getState()) || !ThreadUtil.getUidInt().equals(dbData.getUid())){
			return new ResultModel("ILLEGAL_STATE", "当前数据状态异常");
		}
		servAppointService.invalid(id);
		
		return new ResultModel(true);
	}
	
}
