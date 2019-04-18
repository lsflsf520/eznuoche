
var html = document.getElementsByTagName('html')[0];
var width = window.innerWidth;
var fontSize = 100 / 750 * width;
html.style.fontSize = fontSize + 'px';
window.onresize = function() {
	var html = document.getElementsByTagName('html')[0];
	var width = window.innerWidth;
	var fontSize = 100 / 750 * width;
	html.style.fontSize = fontSize + 'px';
};
var nums = 60;
var invitation_code_val = '';
//获取验证码
var iszhuceh = true;
var uuid = "cms"+guid();
function sendCode(thisBtn) {
	var mobile = $("#phone").val();
	var myreg = /^1[0-9]{10}$/;

	if(!(myreg.test(mobile))) {
		mui.toast("请输入正确的手机号");
		return false;
	}

	//发送验证码接口
	if(iszhuceh){
		btn = thisBtn;
		btn.disabled = true; //将按钮置为不可点击
		btn.value = nums + 's';
		clock = setInterval(doLoop, 1000); //一秒执行一次
		iszhuceh = false;
		function successfn(response,status,xhr){
			iszhuceh = true;
			mui.toast('短信发送成功');
			$(btn).addClass('hover');
		}
		function shibai(response,status,xhr){
			iszhuceh = true;
			nums = 0;
			$(btn).removeClass('hover');
		}
		var obj={
			timeoutFn:function(){
				mui.toast('请求超时')
			}
		};
		mui.toast('正在发送验证码');
		Kino.ajax(chexianapi+'/reg/sms/send.do?phone='+mobile+'&pkey='+uuid,successfn,obj,shibai);
	}
}
//注册
$("#zhuce_ym").on('click', function() {
	if(checkInput()) {
		var phone_val = $('#phone').val();
		invitation_code_val = $('#invitation_code').val();
		if(isNull(invitation_code_val)){
			invitation_code_val = ''
		}else{
			invitation_code_val = $('#invitation_code').val();
		}
		var code_val = $('#code').val();
		var password_val = $('#password').val();
//注册接口
		function successfn(response,status,xhr){
			mui.toast('注册成功');
			setTimeout(function(){
				window.location.href = '../../index.html';
			},2000);
		}
		var obj={
			type:"POST",
			data:{phone:phone_val,password:password_val,fcode:invitation_code_val,_code_:code_val,pkey:uuid},
			timeoutFn:function(){
				mui.toast('请求超时')
			}
		};
		console.log(1234);
		Kino.ajax(chexianapi+'/user/doReg.do',successfn,obj);

	}
});
function S4() {
	return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function guid() {
	return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}

function checkInput() {
	var mobile = $("#phone").val();
	var code = $("#code").val();
	var repassword = $("#password").val();
	var myreg = /^1[0-9]{10}$/;
	if(!(myreg.test(mobile))) {
		mui.toast("请输入正确的手机号");
		return false;
	}else if(code == null || code == '') {
		mui.toast("请输入验证码");
		return false;
	} else if(repassword.length < 6 || password.length > 20) {
		mui.toast("密码必须是6-20位");
		return false;
	}
	return true;
}
function doLoop() {
	nums--;
	if(nums > 0) {
		btn.value = nums + 's';
	} else {
		clearInterval(clock); //清除js定时器
		btn.disabled = false;
		btn.value = '重新发送';
		nums = 60; //重置时间
	}
}

//登录接口
	$("body").on('click','.tx_mi',function() {
		if(checkInput2()) {
			var mobile = $("#phone").val();
			var repassword = $("#password").val();
			//登录请求
			function successfn(response, status, xhr) {
				console.log(response);
				mui.toast('登录成功');
				setTimeout(function(){
					window.location.href = '/page/myself/my.html'
				},2000)
			}
			var obj = {
				type: "POST",
				data: {
					loginName: mobile,
					password: repassword
				},
				timeoutFn: function() {
					mui.toast('请求超时')
				}
			};
			Kino.ajax(chexianapi + '/user/doLogin.do', successfn, obj);

		}
	});

	function checkInput2() {
		var mobile = $("#phone").val();
		var repassword = $("#password").val();
		var myreg = /^1[0-9]{10}$/;
		if(!(myreg.test(mobile))) {
			mui.toast("请输入正确的手机号");
			return false;
		}
		if(repassword.length < 6 || password.length > 20) {
			mui.toast("密码必须是6-20位");
			return false;
		}
		return true;
	}

//找回密码
function zheyanz() {
	var mobile = $("#phone").val();
	var code = $("#code").val();
	var repassword = $("#password").val();
	var myreg = /^1[0-9]{10}$/;
	if(!(myreg.test(mobile))) {
		mui.toast("请输入正确的手机号");
		return false;
	}else if(code == null || code == '') {
		mui.toast("请输入验证码");
		return false;
	} else if(repassword.length < 6 || password.length > 20) {
		mui.toast("密码必须是6-20位");
		return false;
	}
	return true;
}
//按钮调色
function zaohbtn() {
	var mobile = $("#phone").val();
	var code = $("#code").val();
	//var imgcode_val = $("#imgcode").val();
	var repassword = $("#password").val();
	if(mobile == null || mobile == '' || code == null || code == ''|| repassword == null || repassword == ''){
		$('.tx_mi').removeClass('hover')
	}else{
		$('.tx_mi').addClass('hover')
	}
}

//function zhhpwd(){
//	if(zheyanz()){
//		function successfn(response,status,xhr){
//
//			if(response.resultMsg == '成功'){
//				mui.toast('找回密码成功');
//				setTimeout(function(){
//					window.location.href = cs_url;
//				},2000);
//			}else{
//				mui.toast(response.resultMsg);
//			}
//
//		}
//		var obj={
//			type:"POST",
//			data:{phone:phone_val,password:password_val,'fcode':invitation_code_val},
//			timeoutFn:function(){
//				mui.toast('请求超时')
//			}
//		};
//		Kino.ajax(chexianapi+'/user/doReg.do',successfn,obj);
//	}
//}

//获取验证码
var iscode = true;
function sendCode2(thisBtn) {
	var mobile = $("#phone").val();
	var myreg = /^1[0-9]{10}$/;

	if(!(myreg.test(mobile))) {
		mui.toast("请输入正确的手机号");
		return false;
	}

	//发送验证码接口
	if(iscode){
		iscode = true;
		btn = thisBtn;
		btn.disabled = true; //将按钮置为不可点击
		btn.value = nums + 's';
		clock = setInterval(doLoop, 1000); //一秒执行一次
		function successfn(response,status,xhr){
			console.log(response);
			iscode = true;
			mui.toast('短信发送成功');
			$(btn).addClass('hover');
		}
		function shibai(response,status,xhr){
			iscode = true;
			nums=0;
			$(btn).removeClass('hover');
		}
		var obj={
			timeoutFn:function(){
				mui.toast('请求超时')
			}
		};
		mui.toast('正在发送短信验证码，请稍等');
		iscode = false;
		Kino.ajax(chexianapi+'/findpwd/sms/send.do?phone='+mobile+'&pkey='+uuid,successfn,obj,shibai);
	}
}

//找回密码接口
$("#zhanhda").on('click', function() {
	$('.tx_mi').removeClass('hover');
	if(zheyanz()) {
		var phone_val = $('#phone').val();
		var code_val = $('#code').val();
		var password_val = $('#password').val();
//获取随机码
		(function(){
			function successfn(response,status,xhr){
				console.log(response);
				//重置密码
					function successfn(response2,status,xhr){
							mui.toast('找回密码成功');
							setTimeout(function(){
								window.location.href = '/page/login/login.html';
							},2000);
					}

					var obj={
						type:"POST",
						//data:{phone:phone_val,password:password_val,secCode:code_val,pkey:uuid},
						data:{secCode:response.model.data,password:password_val,pkey:uuid},
						timeoutFn:function(){
							mui.toast('请求超时')
						}
					};
					Kino.ajax(chexianapi+'/user/resetPwd.do',successfn,obj);
			}
			var obj={
				timeoutFn:function(){
					mui.toast('请求超时')
				}
			};
			Kino.ajax(chexianapi+'/user/genSecCode.do?phone='+phone_val+'&_code_='+code_val+'&pkey='+uuid,successfn,obj);
		})();



	}
});
