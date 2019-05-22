package com.xyz.eznuoche.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.constant.WxPayConstants.SignType;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.util.SignUtils;
import com.xyz.eznuoche.entity.PayLog;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.eznuoche.service.IBalanceService;
import com.xyz.eznuoche.service.PayLogService;
import com.xyz.eznuoche.service.ThirdUserService;
import com.xyz.tools.cache.redis.RedisDistLock;
import com.xyz.tools.common.bean.ResultModel;
import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.common.constant.OrdState;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.common.utils.IPUtil;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.web.util.WebUtils;
import com.xyz.tools.web.util.WxTool;

@Controller
@RequestMapping("wxpay")
public class WxPayController {
	
	@Resource
	private PayLogService payLogService;
	@Resource
	private IBalanceService balanceService;
	@Resource
	private ThirdUserService thirdUserService;
	
	private static Map<MsgType, Map<Integer/*amount*/, Integer/*num*/>> msg2AmountMap = new HashMap<>();
	
	static {
		Map<Integer, Integer> amount2NumMap = new HashMap<>();
		amount2NumMap.put(1, 10);
		amount2NumMap.put(5, 55);
		amount2NumMap.put(10, 120);
		
		msg2AmountMap.put(MsgType.SMS, amount2NumMap);
	}
	
	/**
	 * h5支付
	 */
	@RequestMapping("mweb")
	@ResponseBody
	public ResultModel mwebPay(int amount, MsgType msgType) {
		Map<Integer, Integer> amount2NumMap = msg2AmountMap.get(msgType);
		if(CollectionUtils.isEmpty(amount2NumMap) || !amount2NumMap.containsKey(amount)) {
			return new ResultModel("ILLEGAL_PARAM", "参数错误");
		}
		
		String outTradeNo = payLogService.genOutTradeNo();
		try {
			ThirdUser tuser = thirdUserService.loadByUid(ThreadUtil.getUidInt());
			if(tuser == null || StringUtils.isBlank(tuser.getThirdUid())) {
				return new ResultModel("ILLEGAL_STATE", "请在微信中打开本页面，并授权绑定账号后重试");
			}
			
			payLogService.genPayLog(ThreadUtil.getUidInt(), msgType, outTradeNo, amount);
			WxPayUnifiedOrderResult pay = WxTool.getWxPayService()
					.unifiedOrder(getOrderReq(outTradeNo, "MWEB",
							amount, msgType.getDescp() + "充值费",
							tuser.getThirdUid(), BaseConfig.getValue("wx.pay.notify.url")));
			return new ResultModel(WebUtils.addParam(pay.getMwebUrl(), "redirect_url",
					WebUtils.urlEncode(BaseConfig.getValue("wx.mweb.redirect.url").trim())));
		} catch (WxPayException e) {
			LogUtils.error("wx mweb pay exception. outTradeNo:%s", e, outTradeNo);
			return new ResultModel(StringUtils.isBlank(e.getErrCode()) ? "WX_PAY_ERR" : e.getErrCode(),
					StringUtils.isBlank(e.getErrCodeDes()) ? "微信支付异常" : e.getErrCodeDes());
		}
	}
	
	@RequestMapping("toPaying")
	public ModelAndView toPaying(){
		
		return new ModelAndView("eznuoche/paying");
	}
	
	/**
	 * 公众号支付
	 */
	@RequestMapping("jsapi")
	@ResponseBody
	public ResultModel jsapiPay(int amount, MsgType msgType) {
		Map<Integer, Integer> amount2NumMap = msg2AmountMap.get(msgType);
		if(CollectionUtils.isEmpty(amount2NumMap) || !amount2NumMap.containsKey(amount)) {
			return new ResultModel("ILLEGAL_PARAM", "参数错误");
		}
		
		String outTradeNo = payLogService.genOutTradeNo();
		try {
			ThirdUser tuser = thirdUserService.loadByUid(ThreadUtil.getUidInt());
			if(tuser == null || StringUtils.isBlank(tuser.getThirdUid())) {
				return new ResultModel("ILLEGAL_STATE", "请在微信中打开本页面，并授权绑定账号后重试");
			}
			
			payLogService.genPayLog(ThreadUtil.getUidInt(), msgType, outTradeNo, amount);
			WxPayUnifiedOrderResult pay = WxTool.getWxPayService()
					.unifiedOrder(getOrderReq(outTradeNo, "JSAPI",
							amount, msgType.getDescp() + "充值费",
							tuser.getThirdUid(), BaseConfig.getValue("wx.pay.notify.url")));
			Map<String, String> params = new LinkedHashMap<>();
			params.put("appId", BaseConfig.getValue("wx.appid"));
			params.put("timeStamp", String.valueOf(System.currentTimeMillis()));
			params.put("nonceStr", pay.getNonceStr());
			params.put("package", "prepay_id=" + pay.getPrepayId());
			params.put("signType", SignType.MD5);
			String paySign = SignUtils.createSign(params, SignType.MD5, BaseConfig.getValue("wx.pay.key"),  null);
			
			LogUtils.info("jsapiPay params:%s, paySign:%s", params, paySign);
			
			return ResultModel.buildMapResultModel().put("params", params).put("paySign", paySign);
		} catch (WxPayException e) {
			LogUtils.error("wx jsapi pay exception. outTradeNo:%s", e, outTradeNo);
			return new ResultModel(StringUtils.isBlank(e.getErrCode()) ? "WX_PAY_ERR" : e.getErrCode(),
					StringUtils.isBlank(e.getErrCodeDes()) ? "微信支付异常" : e.getErrCodeDes());
		}
	}
	
	/**
	 * 支付回调
	 */
	@RequestMapping("callback")
	public void callBack(HttpServletRequest request, HttpServletResponse response, @RequestBody String xmlData) throws WxPayException {
		WxPayNotifyResponse result = new WxPayNotifyResponse();
		if (checkXmlString(xmlData)) {
			LogUtils.warn("callback error, xmlData:%s", xmlData);
			//return new WxPayNotifyResponse("FAIL", "FAIL");
			result = new WxPayNotifyResponse("FAIL", "FAIL");
			WebUtils.writeJson(genXml(result), request, response);
			return;
		}

		WxPayOrderNotifyResult notifyResult = WxTool.getWxPayService().parseOrderNotifyResult(xmlData);
		String outTradeNo = notifyResult.getOutTradeNo();
		LogUtils.info("callback, outTradeNo:%s, resultCode:%s, returnCode:%s", outTradeNo, notifyResult.getResultCode(),
				notifyResult.getReturnCode());

		if ("SUCCESS".equals(notifyResult.getReturnCode()) && "SUCCESS".equals(notifyResult.getResultCode())) {
			PayLog dbData = payLogService.loadByOutTradeNo(outTradeNo);
			if(dbData != null && !OrdState.Payed.equals(dbData.getState())) {
				if(notifyResult.getTotalFee().equals(dbData.getAmount() * 100)) {
					try{
						boolean locked = RedisDistLock.trylock("wxpay_callback", outTradeNo);
						if(locked){
							Map<Integer, Integer> amount2NumMap = msg2AmountMap.get(dbData.getMsgType());
							if(!CollectionUtils.isEmpty(amount2NumMap) && amount2NumMap.containsKey(dbData.getAmount())) {
								balanceService.addBalance(dbData, notifyResult.getTransactionId(), dbData.getMsgType(), amount2NumMap.get(dbData.getAmount()));
								
								LogUtils.info("outTradeNo:%s. handled success", outTradeNo);
							}
						}
					} finally {
						RedisDistLock.release("wxpay_callback", outTradeNo);
					}
				} else {
					LogUtils.warn("amount not equal, outTradeNo:%s, callback amount:%d, payLog amount:%d", outTradeNo, notifyResult.getTotalFee(), dbData.getAmount());
				}
			}
			result = new WxPayNotifyResponse("SUCCESS", "OK");
			WebUtils.writeJson(genXml(result), request, response);
			return;
		}
		LogUtils.warn("callback, outTradeNo:%s. handled fail", outTradeNo);
		result = new WxPayNotifyResponse("FAIL", "FAIL");
		WebUtils.writeJson(genXml(result), request, response);
	}
	
	private String genXml(WxPayNotifyResponse result){
		return "<xml><return_code><![CDATA[" + result.getReturnCode() + "]]></return_code>"
				+ "<return_msg><![CDATA[" + result.getReturnMsg() + "]]></return_msg></xml>";
	}
	
	private boolean checkXmlString(String xmlStr) {
		if (StringUtils.isBlank(xmlStr))
			return false;
		if (xmlStr.indexOf("!DOCTYPE") > -1 || xmlStr.indexOf("!ENTITY") > -1)
			return true;
		return false;
	}
	
	private WxPayUnifiedOrderRequest getOrderReq(String outTradeNo, String tradeType, Integer amount,
			String prodDesc, String openId, String notify) {
		WxPayUnifiedOrderRequest req = new WxPayUnifiedOrderRequest();
		req.setBody(prodDesc);
		req.setOutTradeNo(outTradeNo);
		req.setTotalFee(amount * 100);
		req.setSpbillCreateIp(IPUtil.getLocalIp());
		req.setTradeType(tradeType);
		req.setProductId(amount+"");
		req.setSpbillCreateIp(ThreadUtil.getClientIP());
		req.setOpenid(openId);
		req.setNotifyUrl(notify);
		return req;
	}

}
