package com.xyz.eznuoche.service;

import java.util.HashMap;
import java.util.Set;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.xyz.eznuoche.utils.MsgUtil;
import com.xyz.tools.common.bean.ResultModel;

public class IvrService {
	
	public ResultModel ivrCall(String phone, String mediaTxt, String userdata) {
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init(MsgUtil.SMS_HOST, MsgUtil.SMS_PORT);// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount(MsgUtil.SMS_ACCSID, MsgUtil.SMS_TOKEN);// 初始化主帐号和主帐号TOKEN
		restAPI.setAppId("8a216da85c62c9ad015c99f12e231305");// 初始化应用ID
		
		HashMap<String, Object> resutlMap = restAPI.landingCall(phone, null, mediaTxt, null, "3", 
				"http://eznc.qiyejs.cn/ivr/callback.do", userdata, null, null, null, null, null);
		if("000000".equals(resutlMap.get("statusCode"))){
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) resutlMap.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
				System.out.println(key +" = "+object);
			}
		}else{
			//异常返回输出错误码和错误信息
			System.out.println("错误码=" + resutlMap.get("statusCode") +" 错误信息= "+resutlMap.get("statusMsg"));
		}
		
		return new ResultModel(true);
	}
	
	
	public static void main(String[] args) {
		new IvrService().ivrCall("17773132069", "您的爱车湘AN0L92挡住了别人的去路，请速速前去挪车", "3322");
	}

}
