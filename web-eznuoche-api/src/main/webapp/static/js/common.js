//通用js
/**
 * ajax:ajax通用配置(已经在下面代码处理了防止重复提交跟超时按钮禁用问题，值得注意的是，在按钮点击后需要马上禁用按钮，然后配合封装的ajax做处理)
 * url 为请求地址，successfn 成功回调函数 configs配置对象 可选配置对象 { async:"false", 是否异步
 * type:"POST", 请求get/post dataType:"jsonp", 接受类型 data:{a:1,b:2}, 发送数据对象
 * timeoutFn:function(){ 请求超时回调函数
 *  }, btnSelector:"#save" 防重复提交按钮选择器 id/class 比如 #save / .save } -----
 * renderData：arttemplate通用方法
 * 
 * @arttemplate封装
 * @param {Object需要渲染的对象}
 *            data
 * @param {string}
 *            templateId
 * @param {string}
 *            target arttemplate插入的元素id
 * @param {boolean}
 *            clear true表示$.html false表示$.append
 */


window.Kino = {
    ajax: function (url, successfn, configs,failure,shishiqu) { // ajax封装
        async = (configs.async == null || configs.async == "" || typeof (configs.async) == "undefined") ? "true" : configs.async;
        btnSelector = (configs.btnSelector == null || configs.btnSelector == "" || typeof (configs.btnSelector) == "undefined") ? false : configs.btnSelector;
        type = (configs.type == null || configs.type == "" || typeof (configs.type) == "undefined") ? "get" : configs.type;
        dataType = (configs.dataType == null || configs.dataType == "" || typeof (configs.dataType) == "undefined") ? "json" : configs.dataType;
        data = (configs.data == null || configs.data == "" || typeof (configs.data) == "undefined") ? {
            "date": new Date().getTime()
        } : configs.data;
        on_jiazaiz();
        $.ajax({
            type: type,
            async: async,
            data: data,
            url: url,
            timeout: 20000, // 设置超时时间
            dataType: dataType,
            success: function (response, status, xhr) {
            	
                if (btnSelector){
                    $(btnSelector).prop('disabled', false); // 防止重复提交，请求成功后才激活按钮

                }else if(response.resultCode == 'NOT_LOGON'){
                       mui.toast('请登录后重试');
                       // loginReg();//main.js 显示弹框
                       // console.log(window.location.h)
                }else if(response.resultCode == "ORD_STATUS_ERR"){
                    var urlLocation = getvalue8()[1] +'//'+ getvalue8()[2];
// mui.toast(response.resultMsg)
                    myTooltip({
	            		_thiselent: response.resultMsg
	            	});
// myTooltip(obj);
                }else if(response.resultCode == 'NEED_WX_AUTH'){
                	var urls = decodeURI(location.href);
                	mui.toast(response.resultMsg)
// window.location.href = chexianapi + '/wxuser/wxauth.do?currUrl='+urls;
                	  $.ajax({
				            url: chexianapi + '/wxuser/wxauth.do',
				            type: "post",
				            data: {currUrl:urls},
				            success: function (data_list) {
// console.log(data_list);
				                window.location.href = data_list.model.data;
				            }
				        });
                }else if(response.resultCode == 'NEED_BIND_ACC'){
                	 binding();
                }else if (response.resultCode == 'SUCCESS') {
                    // 已登录
                      successfn(response, status, xhr);
                }
               
              else {
              // 其他错误代码
                    mui.toast(response.resultMsg);
	           // if(response.resultMsg != ''){
	           // mui.toast(response.resultMsg);
	           // }
	                if(failure != undefined){
	                    failure(response, status, xhr)
	                }

              }
            },
            beforeSend: function (xhr) {
            	
            	 off_jiazaiz();
                // 发送ajax请求之前向http的head里面加入验证信息
                var ua = navigator.userAgent.toLowerCase();
                if (/iphone|ipad|ipod/.test(ua)) {
                    if (/LT-APP/.test(navigator.userAgent)) {
                        xhr.setRequestHeader("clientType", "IOS");
                    } else {
                        xhr.setRequestHeader("clientType", "H5");
                    }
                } else {
                    if (/LT-APP/.test(navigator.userAgent)) {
                        xhr.setRequestHeader("clientType", "Android");
                    } else {
                        xhr.setRequestHeader("clientType", "H5");
                    }
                }
            },
            complete: function (XMLHttpRequest, status,xhr) {
                // 隐藏正在加载
              
                if(shishiqu != undefined){
                    shishiqu(XMLHttpRequest, status,xhr);
                }
// if (status == 'timeout') {
// if (btnSelector) {
// $(btnSelector).prop('disabled', false); //超时按钮激活处理
// }
                  // xhr.abort(); // 超时后中断请求
                  // if (typeof configs.timeoutFn === 'function') {
                  // console.log('响应超时，刷新页面');
                  // timeoutFn(); //调取超时回调函数
                  // }
// }
		　		if(status=='timeout'){
		 　　　　　 xhr.abort();
		　　　　　  mui.toast("请求超时");
		　　　　}

            }
        });
    }
};
// 追加请加载中
function on_jiazaiz(){
    var html='';
    html+='<div class="jiz_dongt">'+
    '<div class="mask" style="display: block;"></div>'+
    '<div class="gt_huxzlg" style="display: block;">'+
    '<div></div>'+
    '<p>加载中，请稍后</p>'+
    '</div>'+
    '</div>';
    $('body').append(html);
}
function off_jiazaiz(){
    $('.jiz_dongt').hide();
}
function isNull(data){
    return (data == "" || data == undefined || data == null || typeof(data)=="undefined");
}

