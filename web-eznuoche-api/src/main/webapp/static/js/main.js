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
// 加载遮罩层
zstanc();
zuijdo();
var invitation_code_val = '';
function zstanc() {
	document.write('<div class="jiz_dongt">' + '<div class="mask"></div>'
			+ '<div class="gt_huxzlg">' + '<span></span>' + '<p>加载中，请稍后</p>'
			+ '</div>' + '</div>')
}
// 顶部导航
function zuijdo() {
	$('.hader_zuij').html();
}

// 通用登录注册弹窗
function loginReg() {
	$('.ty_login').remove();
	var loginHtml = ''
	loginHtml = '<div class="ty_login"><div class="mask"></div>'
			+ '<div class="in_sqtz">'
			+ ' <div class="tz_topty">'
			+ '<div class="ty_tcxzk">'
			+ '<span>注册</span>'
			+ '<span class="ss hover">登录</span>'
			+ '</div>'
			+
			// 关闭按钮
			// '<div class="ty_gbbtn"></div>' +
			'</div>'
			+ '<div class="list_log"><div  class="tabin ty_zhuce" style="display: none;">'
			+ '<div class="tx_zhuczh">'
			+ ' <div class="zh_tongysrh bg_shouji">'
			+ '<input id="phone" type="number" placeholder="请输入手机号" pattern="\d*" autocomplete="off"/>'
			+ '<input type="button" value="获取验证码" onclick="sendCode(this)"/>'
			+ '</div>'
			+ '<div class="zh_tongysrh bg_duanxin">'
			+ ' <input id="code" type="text" placeholder="请输入短信验证码" autocomplete="off"/>'
			+ '</div>'
			+ '<div class="zh_tongysrh bg_shrmm">'
			+ ' <input type="password" id="password" placeholder="请输入密码" autocomplete="off"/>'
			+ '</div>'
			+ '<div class="zh_tongysrh bg_srtuijm">'
			+ '<input type="text" id="invitation_code" placeholder="请输入推荐码，没有可不填写" autocomplete="off"/>'
			+ '</div>'
			+ '</div>'
			+ '<div class="sf_tongyxy">'
			+ '<input id="zhuce_ym" type="button" value="注册"/>'
			+

			'<div class="xy_tongyxy"><i></i>我同意 <a id="fuwu_xieyi" href="#">《服务协议》</a></div>'
			+ '</div>' + '</div>'
			+ '<div class="tabin ty_denglu" style="display: block;">'
			+ '<div class="tx_zhuczh">' + '<div class="zh_tongysrh bg_shouji">'
			+ '<input id="phone2" type="text" placeholder="请入手机号"/>' + '</div>'
			+ '<div class="zh_tongysrh bg_shrmm">'
			+ ' <input id="password2" type="password" placeholder="请输入密码"/>'
			+ '</div>' + '</div>' + '<div class="sf_tongyxy">'
			+ '<input class="tx_mi" type="button" value="登录"/>' + '</div>'
			+ '<div class="find"><a href="javascript:;" ">找回密码</a></div>'
			+ '</div>' + '</div>' + '</div>' + '</div>';
	$('body').append(loginHtml);
	// 服务协议 找回密码
	(function() {
		var url = decodeURI(location.href);
		var tmp1 = url.split("/");
		var tmp2 = tmp1[0];
		var tmp3 = tmp1[2];
		// return [tmp2,tmp3];
		// console.log(tmp2+'//'+tmp3+'/page/service_agreement.html'+111);
		$('#fuwu_xieyi').attr('href',
				tmp2 + '//' + tmp3 + '/page/service_agreement.html');
		$('.find a').attr('href',
				tmp2 + '//' + tmp3 + '/page/login/retrieve_password.html')
	})()

	// 登录
	$('.ty_tcxzk>span').each(function(i) {
		var _this = $(this)
		$(_this).on('click', function() {
			$(this).addClass('hover').siblings().removeClass('hover');
			if ($(this).hasClass('hover')) {
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
// 关闭
$('body').on('tap', '.ty_gbbtn', function() {
	$('.ty_login').hide();
});

// 登录注册接口
var nums = 60;
// 获取验证码
var iszhuceh = true;
var uuid = "eznc" + guid();

sendCode = function(thisBtn) {
	var mobile = $("#plate_phone").val();
	var myreg = /^1[0-9]{10}$/;

	if (!(myreg.test(mobile))) {
		mui.toast("请输入正确的手机号");
		return false;
	}

	// 发送验证码接口
	if (iszhuceh) {
		btn = thisBtn;
		btn.disabled = true; // 将按钮置为不可点击
		btn.value = nums + 's';
		clock = setInterval(doLoop, 1000); // 一秒执行一次
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
			timeoutFn : function() {
				mui.toast('请求超时')
			}
		};
		mui.toast('正在发送验证码');
		Kino.ajax('/bindUser/sms/send.do?phone=' + mobile + '&pkey=' + uuid,
				successfn, obj, shibai);
	}
};

$("body").on('tap', '#add_ym', function() {
	var plateNo = $('#plate_number').val();
	if (!plateNo) {
		mui.toast('车牌号不能为空');
		return;
	}
	plateNo = $('#mySel').text() + plateNo;
	// 注册接口
	function successfn(response, status, xhr) {
		mui.toast('登记成功');
		window.location.reload();
	}
	var obj = {
		type : "POST",
		data : {
			plateNo : plateNo
		},
		timeoutFn : function() {
			mui.toast('请求超时')
		}
	};
	Kino.ajax('/addPlateNo.do', successfn, obj);
})

// 注册
$("body").on(
		'tap',
		'#zhuce_ym',
		function() {
			if (checkInput()) {
				var phone_val = $('#plate_phone').val();
				var fcode = $('#plate_invite_code').val();
				if (isNull(fcode)) {
					fcode = ''
				}
				var code_val = $('#plate_smscode').val();
				var plateNo = $('#plate_number').val() ? $('#mySel').text()
						+ $('#plate_number').val() : "";
				// 注册接口
				function successfn(response, status, xhr) {
					mui.toast('登记成功');
					// setTimeout(function() {
					// window.location.href = 'login.html';
					// }, 2000);
					window.location.reload();
					// $('.ty_login').hide();
				}
				var obj = {
					type : "POST",
					data : {
						phone : phone_val,
						plateNo : plateNo,
						fcode : fcode,
						_code_ : code_val,
						pkey : uuid
					},
					timeoutFn : function() {
						mui.toast('请求超时')
					}
				};
				Kino.ajax('/bindUser.do', successfn, obj);

			}
		});

function S4() {
	return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
}

function guid() {
	return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4()
			+ S4() + S4());
}

function checkInput() {
	var mobile = $("#plate_phone").val();
	var code = $("#plate_smscode").val();
	var plateNo = $('#plate_number').val();
	var fcode = $('#plate_invite_code').val();
	var myreg = /^1[0-9]{10}$/;
	if (!(myreg.test(mobile))) {
		mui.toast("请输入正确的手机号");
		return false;
	} else if (code == null || code == '') {
		mui.toast("请输入验证码");
		return false;
	} else if (plateNo
			&& (plateNo.length < 5 || plateNo.length > 10 || !/\w+/
					.test(plateNo))) {
		mui.toast("请输入正确的车牌号");
		return false;
	}
	return true;
}

function doLoop() {
	nums--;
	if (nums > 0) {
		btn.value = nums + 's';
	} else {
		clearInterval(clock); // 清除js定时器
		btn.disabled = false;
		btn.value = '重新发送';
		nums = 60; // 重置时间
	}
}

var platePrefix = [ '湘', '京', '沪', '粤', '津', '渝', '川', '冀', '苏', '晋', '蒙', '辽',
		'吉', '黑', '浙', '皖', '闽', '赣', '鲁', '豫', '鄂', '桂', '琼', '黔', '滇', '藏',
		'陕', '甘', '青', '宁', '新', '台', '港', '澳' ];
function selectPrefix(currSel) {
	var tooltip = '';
	tooltip += '<div class="shade"></div><div class="mask_pull">';
	tooltip += '<div class="mask_pull_tit flex"><span class="mask_pull_name">车牌号码</span>';
	tooltip += '<span class="pull-icon-close"><i class="iconfont icon-guanbi"></i></span></div>';
	tooltip += '<ul class="mask_pull_ul wrapul mask_pulls flex" data-length='
			+ platePrefix.length + '>';
	for (var i = 0; i < platePrefix.length; i++) {
		tooltip += '<li>'
		tooltip += '<a href="javascript:void(0)">' + platePrefix[i] + '</a>'
		tooltip += '<span class="correct iconfont icon-weibiaoti14"></span></li>'
	}
	tooltip += '</ul>'

	tooltip += '</div>';
	$('body').append(tooltip);
	$('.mask_pulls li').each(
			function(i) {
				$(this).on(
						'tap',
						function() {
							$(this).addClass('mask_hover');
							$(this).siblings().removeClass('mask_hover');
							$(this).find('.correct').addClass('correct_hover');
							$(this).siblings().find('.correct').removeClass(
									'correct_hover');
							$(currSel + '.selectVal').html(
									$(this).find('a').html());
							$(currSel + '.selectVal').val(
									$(this).find('a').html());
							$(currSel + '.selectVal').attr('data-code',
									$(this).attr('data-code'));
							$(".mask_pull").slideUp(200);
							$('.shade').remove();
						})
			});
}

$("body").on('tap', '#wxNotify', function() {
	notify('WX', '#wxNotify');
});
$("body").on('tap', '#smsNotify', function() {
	notify('SMS', '#smsNotify');
});
$("body").on('tap', '#telNotify', function() {
	notify('TEL', '#telNotify');
});

function notify(msgType, tdId) {
	var leftCnt = $(tdId).attr('cnt');
	if (leftCnt && parseInt(leftCnt) <= 0) {
		showChargeDialog(msgType);
		mui.toast("剩余次数不够，请先充值或分享本页面邀请好友可获取5次免费通知机会");
		return;
	}
	var plateNo = $('#target_plate_number').val();
	if (!plateNo) {
		mui.toast("对方车牌号不能为空");
		return;
	}
	plateNo = $('#targetSel').text() + plateNo;
	Kino.ajax('/' + msgType + '/notify.do', function(response, status, xhr) {
		mui.toast("已成功通知对方，请耐心等待");
		window.location.reload();
	}, {
		type : "POST",
		data : {
			plateNo : plateNo
		},
		timeoutFn : function() {
			mui.toast('请求超时')
		}
	}, function(response, status, xhr) {
		if ("NOT_ENOUGH_SMS" == response.resultCode) {
			showChargeDialog("SMS");
		} else if ("NOT_ENOUGH_TEL" == response.resultCode) {
			showChargeDialog("TEL");
		}
	});
}

function selectAmount(divId) {
	$('#' + divId).addClass('hover').siblings().removeClass('hover');
}

function hideChargeDialog() {
	$("#maskDiv").hide();
	$("#chargeDiv").hide();
}

function showChargeDialog(msgType) {
	$("#chargeDiv").attr("msg-type", msgType);
	$("#maskDiv").show();
	$("#chargeDiv").show();
}
