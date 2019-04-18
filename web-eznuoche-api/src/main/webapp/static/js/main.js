/**
 * Created by Administrator on 2018/7/25.
 */
var html = document.getElementsByTagName('html')[0];
var width = window.innerWidth;
var fontSize = 100 / 750 * width;
html.style.fontSize = fontSize + 'px';
window.onresize = function() {
	var html = document.getElementsByTagName('html')[0];
	var width = window.innerWidth;
	var fontSize = 100 / 750 * width;
	html.style.fontSize = fontSize + 'px';
}
//加载遮罩层
zstanc();
zuijdo();
var  invitation_code_val = '';
function zstanc() {
	document.write('<div class="jiz_dongt">' +
		'<div class="mask"></div>' +
		'<div class="gt_huxzlg">' +
		'<span></span>' +
		'<p>加载中，请稍后</p>' +
		'</div>' +
		'</div>')
}
//顶部导航
function zuijdo() {
	$('.hader_zuij').html();
}



//通用登录注册弹窗
function loginReg() {
	$('.ty_login').remove();
	var loginHtml = ''
	loginHtml = '<div class="ty_login"><div class="mask"></div>' +
		'<div class="in_sqtz">' +
		' <div class="tz_topty">' +
		'<div class="ty_tcxzk">' +
		'<span>注册</span>' +
		'<span class="ss hover">登录</span>' +
		'</div>' +
		//关闭按钮
		//'<div class="ty_gbbtn"></div>' +
		'</div>' +
		'<div class="list_log"><div  class="tabin ty_zhuce" style="display: none;">' +
		'<div class="tx_zhuczh">' +
		' <div class="zh_tongysrh bg_shouji">' +
		'<input id="phone" type="number" placeholder="请输入手机号" pattern="\d*" autocomplete="off"/>' +
		'<input type="button" value="获取验证码" onclick="sendCode(this)"/>' +
		'</div>' +
		'<div class="zh_tongysrh bg_duanxin">' +
		' <input id="code" type="text" placeholder="请输入短信验证码" autocomplete="off"/>' +
		'</div>' +
		'<div class="zh_tongysrh bg_shrmm">' +
		' <input type="password" id="password" placeholder="请输入密码" autocomplete="off"/>' +
		'</div>' +
		'<div class="zh_tongysrh bg_srtuijm">' +
		'<input type="text" id="invitation_code" placeholder="请输入推荐码，没有可不填写" autocomplete="off"/>' +
		'</div>' +
		'</div>' +
		'<div class="sf_tongyxy">' +
		'<input id="zhuce_ym" type="button" value="注册"/>' +

		'<div class="xy_tongyxy"><i></i>我同意 <a id="fuwu_xieyi" href="#">《服务协议》</a></div>' +
		'</div>' +
		'</div>' +
		'<div class="tabin ty_denglu" style="display: block;">' +
		'<div class="tx_zhuczh">' +
		'<div class="zh_tongysrh bg_shouji">' +
		'<input id="phone2" type="text" placeholder="请入手机号"/>' +
		'</div>' +
		'<div class="zh_tongysrh bg_shrmm">' +
		' <input id="password2" type="password" placeholder="请输入密码"/>' +
		'</div>' +
		'</div>' +
		'<div class="sf_tongyxy">' +
		'<input class="tx_mi" type="button" value="登录"/>' +
		'</div>' +
		'<div class="find"><a href="javascript:;" ">找回密码</a></div>'+
		'</div>' +
		'</div>' +
		'</div>' +
		'</div>';
	$('body').append(loginHtml);
	//服务协议 找回密码
	(function(){
		var url = decodeURI(location.href);
		var tmp1 = url.split("/");
		var tmp2 = tmp1[0];
		var tmp3 = tmp1[2];
		//return [tmp2,tmp3];
//		console.log(tmp2+'//'+tmp3+'/page/service_agreement.html'+111);
		$('#fuwu_xieyi').attr('href',tmp2+'//'+tmp3+'/page/service_agreement.html');
		$('.find a').attr('href',tmp2+'//'+tmp3+'/page/login/retrieve_password.html')
	})()

//	登录
	$('.ty_tcxzk>span').each(function(i){
		var _this = $(this)
		$(_this).on('click',function(){
			$(this).addClass('hover').siblings().removeClass('hover');
			if($(this).hasClass('hover')){
				$('.list_log').find('.tabin').eq(i).show().siblings().hide();
			}
		});
	})

	 $('.ty_login').show();
     $('.ty_login .mask').show();

}
	function dlzc_login() {
		$('.ty_login').html();
	}
//	关闭
	$('body').on('tap','.ty_gbbtn',function(){
		$('.ty_login').hide();
	});

	
		//登录注册接口
	var nums = 60;
	//获取验证码
	var iszhuceh = true;
	var uuid = "cms" + guid();

	sendCode = function(thisBtn) {
		var mobile = $("#phone").val();
		var myreg = /^1[0-9]{10}$/;

		if(!(myreg.test(mobile))) {
			mui.toast("请输入正确的手机号");
			return false;
		}

		//发送验证码接口
		if(iszhuceh) {
			btn = thisBtn;
			btn.disabled = true; //将按钮置为不可点击
			btn.value = nums + 's';
			clock = setInterval(doLoop, 1000); //一秒执行一次
			iszhuceh = false;

			function successfn(response, status, xhr) {
				iszhuceh = true;
				mui.toast('短信发送成功');
				$(btn).addClass('hover');
			}

			function shibai(response, status, xhr) {
				iszhuceh = true;
				nums = 0;
				$(btn).removeClass('hover');
			}
			var obj = {
				timeoutFn: function() {
					mui.toast('请求超时')
				}
			};
			mui.toast('正在发送验证码');
			Kino.ajax(chexianapi + '/reg/sms/send.do?phone=' + mobile + '&pkey=' + uuid, successfn, obj, shibai);
		}
	};

	//注册
	$("body").on('tap','#zhuce_ym', function() {
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
			function successfn(response, status, xhr) {
				mui.toast('注册成功');
//				setTimeout(function() {
//					window.location.href = 'login.html';
//				}, 2000);
				window.location.reload();
				$('.ty_login').hide();
			}
			var obj = {
				type: "POST",
				data: {
					phone: phone_val,
					password: password_val,
					fcode: invitation_code_val,
					_code_: code_val,
					pkey: uuid
				},
				timeoutFn: function() {
					mui.toast('请求超时')
				}
			};
			Kino.ajax(chexianapi + '/user/doReg.do', successfn, obj);

		}
	});

	function S4() {
		return(((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
	}

	function guid() {
		return(S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
	}

	function checkInput() {
		var mobile = $("#phone").val();
		var code = $("#code").val();
		var repassword = $("#password").val();
		var myreg = /^1[0-9]{10}$/;
		if(!(myreg.test(mobile))) {
			mui.toast("请输入正确的手机号");
			return false;
		} else if(code == null || code == '') {
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
			var mobile = $("#phone2").val();
			var repassword = $("#password2").val();
			//登录请求
			function successfn(response, status, xhr) {
//				console.log(response);
				if(response.resultMsg == "成功") {

					try {
						//尝试建立连接
						initWs();
					} catch(e) {
					}
					mui.toast('登录成功');
//					window.location.reload() ;
                    if(document.title == '特种车保险'){

                    }else{
                    	window.location.reload();
                    }
					$('.ty_login').hide()

				} else {
					mui.toast(response.resultMsg);
				}
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
		var mobile = $("#phone2").val();
		var repassword = $("#password2").val();
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
	

function binding(){
	$('.binding').remove();
	var loginHtml = ''
	loginHtml = '<div class="binding"><div class="bindingMask"></div>' +
		'<div class="in_sqtz">' +
		' <div class="tz_topty">' +
		'<div class="ty_tcxzk">' +
		'<span>账号绑定</span>' +
		'</div>' +
		'<div class="ty_gbbtn1"></div>' +
		'</div>' +
		'<div class="list_log"><div  class="tabin ty_zhuce">' +
		'<div class="tx_zhuczh">' +
		' <div class="zh_tongysrh bg_shouji">' +
		'<input id="phone7" type="number" placeholder="请输入手机号" pattern="\d*" autocomplete="off"/>' +
		'<input type="button" value="获取验证码" onclick="bindCode(this)"/>' +
		'</div>' +
		'<div class="zh_tongysrh bg_duanxin">' +
		' <input id="code7" type="text" placeholder="请输入短信验证码" autocomplete="off"/>' +
		'</div>' +
		'</div>' +
		'<div class="sf_tongyxy">' +
		'<input id="binding" type="button" value="绑定账号"/>' +
		'</div>' +
		'</div>' +
		'</div>' +
		'</div>' +
		'</div>'


	$('body').append(loginHtml);
	$('.bindingMask').show();
}
	$('body').on('tap','.ty_gbbtn1',function(){
		$('.binding').hide();
		off_jiazaiz();
	});
	//绑定账号
	$("body").on('click','#binding', function() {
		if(checkInput1()) {
			var mobile = $("#phone7").val();
			var code = $("#code7").val();
			//注册接口
			function successfn(response, status, xhr) {
				console.log(data)
				mui.toast('绑定成功')
				$('.bindingMask').hide();
				var  historyurl = getvalue8()[0] + "//" +getvalue8()[2] + "/main.html"
				window.location.href = historyurl 
				$('#binding').hide();
				off_jiazaiz();
			}
			var obj = {
				type: "POST",
				data: {
					phone: mobile,
					_code_: code,
					pkey: uuid
				},
				timeoutFn: function() {
					mui.toast('请求超时')
				}
			};
			Kino.ajax(chexianapi + '/user/bindUser.do', successfn, obj);

		}
	});
//	验证
	function checkInput1() {
		var mobile = $("#phone7").val();
		var code = $("#code7").val();
		var myreg = /^1[0-9]{10}$/;
		if(!(myreg.test(mobile))) {
			mui.toast("请输入正确的手机号");
			return false;
		}
		if(code == '') {
			mui.toast("验证码不能为空");
			return false;
		}
		return true;
	}
	bindCode = function(thisBtn) {
		var mobile = $("#phone7").val();
		var myreg = /^1[0-9]{10}$/;

		if(!(myreg.test(mobile))) {
			mui.toast("请输入正确的手机号");
			return false;
		}

		//发送验证码接口
		if(iszhuceh) {
			btn = thisBtn;
			btn.disabled = true; //将按钮置为不可点击
			btn.value = nums + 's';
			clock = setInterval(doLoop, 1000); //一秒执行一次
			iszhuceh = false;
			function successfn(response, status, xhr) {
				iszhuceh = true;
				mui.toast('短信发送成功');
				$(btn).addClass('hover');
			}

			function shibai(response, status, xhr) {
				iszhuceh = true;
				nums = 0;
				$(btn).removeClass('hover');
			}
			var obj = {
				timeoutFn: function() {
					mui.toast('请求超时')
				}
			};
			mui.toast('正在发送验证码');
			Kino.ajax(chexianapi + '/bindUser/sms/send.do?phone=' + mobile + '&pkey=' + uuid, successfn, obj, shibai);
		}
	};



