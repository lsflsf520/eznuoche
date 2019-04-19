package com.xyz.eznuoche.controller;




import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xyz.eznuoche.dto.Sms;
import com.xyz.eznuoche.dto.SmsDto;
import com.xyz.eznuoche.service.SmsService;
import com.xyz.tools.common.bean.ResultModel;
import com.xyz.tools.common.utils.ThreadUtil;

@Controller
@RequestMapping("sms")
public class SmsController {
	
	@Resource
	private SmsService smsService;

	@RequestMapping(value="send", method=RequestMethod.POST)
	@ResponseBody
	public ResultModel send(Sms sms){
		sms.setSrcIP(ThreadUtil.getSrcIP());
		return smsService.send(sms);
	}
	
	@RequestMapping(value="asyncsend", method=RequestMethod.POST)
	@ResponseBody
	public ResultModel asyncsend(Sms sms){
		sms.setSrcIP(ThreadUtil.getSrcIP());
		return smsService.asyncSend(sms);
	}
	
	@RequestMapping(value="batchSend", method=RequestMethod.POST)
	@ResponseBody
	public ResultModel batchSend(SmsDto smsdto){
		/*if(smsdto == null || smsdto.getSmsList() == null || smsdto.getSmsList().length <= 0){
			return new ResultModel("ILLEGAL_PARAM", "短信参数不能为空");
		}*/
		if(smsdto == null || CollectionUtils.isEmpty(smsdto.getSmsList())){
			return new ResultModel("ILLEGAL_PARAM", "短信参数不能为空");
		}
		
		for(Sms sms : smsdto.getSmsList()){
			sms.setSrcIP(ThreadUtil.getSrcIP());
		}
		
		return smsService.asyncSend(smsdto.getSmsList().toArray(new Sms[0]));
	}
	
}
