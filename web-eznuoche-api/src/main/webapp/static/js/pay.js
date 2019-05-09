//微信支付
function weixinpay() {
	if (is_weixnNag()) {// 如果是微信浏览器，则使用微信公众号支付方式
		inweixinpay();
	} else {
		Kino.ajax("/wxpay/mweb.do", function(response) {
			if (response.resultCode == 'SUCCESS') {
				setCookie("orderorg", document.referrer);
				setCookie("payid", showTradeNo);// 记录下当前订单id
				console.log(getCookie("orderorg"));
				window.location = response.model.data;
			} else {
				mui.toast("订单已支付，请勿重复提交");
				setTimeout(goback, 1000);
			}
		}, {
			data : {
				orderId : payid,
			},
			headers : {
				'clientType' : 'H5'
			},
			type : "post"
		});
	}
}

// 微信公众号支付
function inweixinpay() {
	var openid = getParam("openId");
	if (!openid || openid == "") {
		mui.toast("微信授权失败");
		return;
	}
	Kino.ajax("/wxpay/jsapi.do", function(response) {// 支付
		if (response.resultCode == 'SUCCESS') {
			setCookie("orderorg", document.referrer);
			setCookie("payid", showTradeNo);// 记录下当前订单id
			onBridgeReady(response.model);
		} else {
			mui.toast("订单已支付，请勿重复提交");
			setTimeout(goback, 1000);
		}
	}, {
		data : {
			orderId : payid,
			openId : openid
		},
		headers : {
			'clientType' : 'H5'
		},
		type : "post"
	});
}

function onBridgeReady(data) {
	WeixinJSBridge.invoke('getBrandWCPayRequest', {
		"appId" : data.params.appId, // 公众号名称，由商户传入
		"timeStamp" : data.params.timeStamp, // 时间戳，自1970年以来的秒数
		"nonceStr" : data.params.nonceStr, // 随机串
		"package" : data.params.package,
		"signType" : data.params.signType, // 微信签名方式：
		"paySign" : data.paySign, // 微信签名
	}, function(res) {
		console.log(res);
		if (res.err_msg == "get_brand_wcpay_request:ok") {
			// 使用以上方式判断前端返回,微信团队郑重提示：
			// res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
			window.location = "/wxpay/paying.do";
		}
	});
}