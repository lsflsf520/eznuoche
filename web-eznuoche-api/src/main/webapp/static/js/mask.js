var phoneNumber =null;
var webtitle=null
var keywords =null;
var description =null;
var cashable = null;
var web_currency = null;
var web_dict_main = null;
var index_lunbo_image = null;
	/**
 *   提示框
 * @param obj  obj类型为对象， 其属性有:
 * obj.title        弹窗的顶部的标题     默认为所需证件
 * obj.textContent  中间内容文字     
 * obj.addContarner 弹窗加在那个容器上   默认为body
 * obj.btnclose     关闭按钮
 * obj.sureFunction    回调函数
 * @constructor
 */
function ShowTooltip(obj) {
    var title = ""
    var addContainer = "body";
    var _thiselent = "";
    var attrContarner = {
			item: []
		}
    var sureFunction = function () {
    }
    var backfun = function () {
    }
    if (arguments.length > 0) {
        if (typeof obj == "object") {
            title = obj.title || title;
            addContainer = obj.addContarner || addContainer;
            sureFunction = obj.sureFunction || sureFunction;
            attrContarner = obj.attrContarner || attrContarner;
            _thiselent = obj._thiselent || _thiselent;
            backfun = obj.backfun || backfun;
        } else {
            console.log("参数类型错误")
        }
    }
    var tooltipHtml = ''
	var tooltipHtml ='<div class="shade"></div><div class="mask_pull"><div class="mask_pull_tit flex"style="padding-right:0rem" ><span class="mask_pull_name">' + title + '</span>'+
					'<span class="pull-icon-close"  style="display: inline-block; padding: 0.3rem;"><i class="iconfont icon-guanbi"></i></span></div>'+
					'<ul class="mask_pull_ul" data-length='+ attrContarner.item.length +'>'+
						'<script id="template" type="text/html">{{each item as value index}}<li  class="flex" data-abbreviation="{{value.shortName}}" data-code="{{value.code}}"><a href="javascript:void(0)">{{value.name}}</a><span class="correct iconfont icon-weibiaoti14"></span></li>'+
						'{{/each}}</script>'+
					'</ul>'+
				'</div>'
     $(addContainer).append(tooltipHtml);
     var html = template("template", attrContarner);
	 $('.mask_pull_ul').append(html);
	 $('.mask_pull_ul li').each(function(i){
		 $(this).on('tap',function(){
			$(this).addClass('mask_hover');
			$(this).siblings().removeClass('mask_hover');
			$(this).find('.correct').addClass('correct_hover');
			$(this).siblings().find('.correct').removeClass('correct_hover');
			$('.'+_thiselent).html($(this).find('a').html());
			$('.'+_thiselent).val($(this).find('a').html());
			//sureFunction($(this).index(),$(this).parent().attr('data-length'));
			$('.'+_thiselent).attr('data-code',$(this).attr('data-code'));
			 //险种为不投保是处理
			 if($(this).find('a').html() == '不投保'){
				 sureFunction($('.'+_thiselent).prev(),false)
			 }else{
				 sureFunction($('.'+_thiselent).prev(),true)
			 }
			 //所属性质为个人或企业变更对应的证件类型
			 if($(this).find('a').html() == '个人'){
				 sureFunction($('.wo_zjleix'),'个人')
			 }else if($(this).find('a').html() == '企业'){
				 sureFunction($('.wo_zjleix'),'企业')
			 }
			$(".mask_pull").slideUp(200);
    		$('.shade').remove();
    		backfun();
		})
	});
}
$(document.body).on("tap", ".pull-icon-close", function () {
     $(".mask_pull").slideUp(200);
     $('.shade').remove();
});
$(document.body).on("click", ".shade", function () {
     $(".mask_pull").slideUp(200);
     $('.shade').remove()
 })



//简称
	/**
 *   提示框
 * @param obj  obj类型为对象， 其属性有:
 * obj.title        弹窗的顶部的标题     默认为所需证件
 * obj.textContent  中间内容文字     
 * obj.addContarner 弹窗加在那个容器上   默认为body
 * obj.btnclose     关闭按钮
 * obj.sureFunction    回调函数
 * @constructor
 */
function addreviation(obj) {
    var title = ""
    var addContainer = "body";
    var _thiselent = "";
    var attrContarner = {
			item: []
		}
    var sureFunction = function () {
    }
    var backfun = function () {
    }
    if (arguments.length > 0) {
        if (typeof obj == "object") {
            title = obj.title || title;
            addContainer = obj.addContarner || addContainer;
            sureFunction = obj.sureFunction || sureFunction;
            attrContarner = obj.attrContarner || attrContarner;
            _thiselent = obj._thiselent || _thiselent;
            backfun = obj.backfun || backfun;
        } else {
            console.log("参数类型错误")
        }
    }
    var tooltipHtml = ''
	var tooltipHtml ='<div class="shade"></div><div class="mask_pull"><div class="mask_pull_tit flex"style="padding-right:0rem" ><span class="mask_pull_name">' + title + '</span>'+
					'<span class="pull-icon-close"  style="display: inline-block; padding: 0.3rem;"><i class="iconfont icon-guanbi"></i></span></div>'+
					'<ul class="mask_pull_ul" data-length='+ attrContarner.item.length +'>'+
						'<script id="template01" type="text/html">{{each item as value index}}<li  class="flex" data-abbreviation="{{value.shortName}}" data-code="{{value.code}}"><a href="javascript:void(0)">{{value.shortName}}</a><span class="correct iconfont icon-weibiaoti14"></span></li>'+
						'{{/each}}</script>'+
					'</ul>'+
				'</div>'
     $(addContainer).append(tooltipHtml);
     var html = template("template01", attrContarner);
	 $('.mask_pull_ul').append(html);
	 $('.mask_pull_ul li').each(function(i){
		 $(this).on('tap',function(){
			$(this).addClass('mask_hover');
			$(this).siblings().removeClass('mask_hover');
			$(this).find('.correct').addClass('correct_hover');
			$(this).siblings().find('.correct').removeClass('correct_hover');
			$('.'+_thiselent).html($(this).attr('data-abbreviation'));
			$('.'+_thiselent).val($(this).attr('data-abbreviation'));
			//sureFunction($(this).index(),$(this).parent().attr('data-length'));
			$('.'+_thiselent).attr('data-code',$(this).attr('data-code'));
			 if($(this).find('a').html() == '不投保'){
				 sureFunction($('.'+_thiselent).prev())
			 }
			$(".mask_pull").slideUp(200);
    		$('.shade').remove();
    		backfun();
    		
		})
	});
}
	/**
 *   头部
 * @param obj  obj类型为对象， 其属性有:
 * obj.title        顶部的标题     默认为
 * obj.content    如果是文字，有内容     
 * obj.icontit    是文字还是图标
 * obj.sureFunction    回调函数
 * @constructor
 */

QuoteResult = function(data){
	console.log(data)
	for (var i=0;i<data.model.length;i++) {
		if(data.model[i].code == 'd_web_tel'){
			phoneNumber =  JSON.parse(data.model[i].addtion).value;
		}else if(data.model[i].code == 'd_web_title'){
			webtitle =  JSON.parse(data.model[i].addtion).value;
			var tmp1 = document.title;
			document.title = ''+tmp1+'_'+webtitle+'';
			
		}else if(data.model[i].code == 'd_web_keywords'){
			keywords =  JSON.parse(data.model[i].addtion).value;
			var url = decodeURI(location.href);
			var tmp1 = url.split("/");
			var tmp2 = (tmp1[tmp1.length-1]).split('.')[0];
			if (tmp2 == 'main'|| tmp2 == '') {
				addMeta('Keywords',keywords);
			} 
		}else if(data.model[i].code == 'd_web_description'){
			description =  JSON.parse(data.model[i].addtion).value;
			var url = decodeURI(location.href);
			var tmp1 = url.split("/");
			var tmp2 = (tmp1[tmp1.length-1]).split('.')[0];
			if (tmp2 == 'main' || tmp2 == '') {
				addMeta('description',description);
			} 
		}else if(data.model[i].code == 'd_web_cashable'){
//			cashable =  JSON.parse(data.model[i].addtion).value;
//			
//			console.log(document.title == '特种车保险')
//			if (document.title == '特种车保险') {
//				addMeta('description',description)
//			} 
		}else if(data.model[i].code == 'd_web_currency'){
			web_currency =  JSON.parse(data.model[i].addtion).value;
		}else if(data.model[i].code == 'd_main_lunbo_image'){
			web_dict_main =  JSON.parse(data.model[i].addtion).value;

		try{
				//console.log(web_dict_main.splice(',')[0])
			if(web_dict_main.indexOf(',')==(-1)){
				var html = '';
				var html2 = '';
				html+='  <div class="mui-slider-item mui-slider-item-duplicate">'+
				'<a href="'+web_dict_main.split('|')[0]+'">'+
				'<img id="shey_lunb_jiedt0" src="'+web_dict_main.split('|')[1]+'">'+
				'</a>'+
				'</div>'+

				'<div class="mui-slider-item">'+
				'<a href="'+web_dict_main.split('|')[0]+'">'+
				'<img id="shey_lunb_jiedt1" src="'+web_dict_main.split('|')[1]+'">'+
				'</a>'+
				'</div>'+

				'<div class="mui-slider-item mui-slider-item-duplicate">'+
				'<a href="'+web_dict_main.split('|')[0]+'">'+
				'<img src="'+web_dict_main.split('|')[1]+'">'+
				'</a>'+
				'</div>';
				html2+='<div class="mui-indicator mui-active"></div>';
				$('#ew_zengj_zjied').html(html);
				$('#cds_dzhij_xiab').html(html2);
			}else{
//				console.log(web_dict_main.split(','))
				var html = '';
				var html2 = '';
				for(var j=0;j<web_dict_main.split(',').length;j++){
					if(j==0){
						html+='  <div class="mui-slider-item mui-slider-item-duplicate">'+
						'<a href="'+(web_dict_main.split(',')[web_dict_main.split(',').length-1]).split('|')[0]+'">'+
						'<img  src="'+(web_dict_main.split(',')[web_dict_main.split(',').length-1]).split('|')[1]+'">'+
						'</a>'+
						'</div>';
						html2+='<div class="mui-indicator mui-active"></div>'
					}
					html+='<div class="mui-slider-item">'+
					'<a href="'+(web_dict_main.split(',')[j]).split('|')[0]+'">'+
					'<img  src="'+(web_dict_main.split(',')[j]).split('|')[1]+'">'+
					'</a>'+
					'</div>';
					if(j==web_dict_main.split(',').length-1){
						html+='<div class="mui-slider-item mui-slider-item-duplicate">'+
						'<a href="'+(web_dict_main.split(',')[0]).split('|')[0]+'">'+
						'<img src="'+(web_dict_main.split(',')[0]).split('|')[1]+'">'+
						'</a>'+
						'</div>';
					}
					if(j!=0){
						html2+='<div class="mui-indicator"></div>'
					}
				}
				$('#ew_zengj_zjied').html(html);
				$('#cds_dzhij_xiab').html(html2);
			}
		}catch(e){
		}

//			console.log(web_dict_main +'33')
		}else if(data.model[i].code == 'd_index_lunbo_image'){
			index_lunbo_image =  JSON.parse(data.model[i].addtion).value;
		}
		mui.init({
			swipeBack:true //启用右滑关闭功能
		});
		var slider = mui("#slider");
		slider.slider({
			interval: 5000
		});
	}
}

function addMeta(name,content){//手动添加mate标签
	let meta = document.createElement('meta');
    meta.content=content;
    meta.name=name;
     document.getElementsByTagName('title')[0].after(meta);
     
}
function titTooltip(obj) {
    var title = "";
    var content = "";
    var icontit = false;
    var addContainer = "body";
    var sureFunction = function () {
    }
    if (arguments.length > 0) {
        if (typeof obj == "object") {
            title = obj.title || title;
            addContainer = obj.addContarner || addContainer;
            icontit = obj.icontit || icontit;
            content = obj.content || content;
        } else {
            console.log("参数类型错误")
        }
    }
    var titletipHtml = ''
	        titletipHtml +='<div class="hader_zuij">'
		    titletipHtml +='<div class="ij_hader">'
		    titletipHtml +='<div class="er_top"><a class="iconfont icon-jiantou-you" href="javascript:;"></a></div>'
			titletipHtml +='<div class="er_cet">'+ title.split('_')[0]+'</div>'
			         if(icontit){
			titletipHtml += '<div class="er_btm er_btm"></div>'
			     	    }else{
			titletipHtml +='<div class="er_btm er_btm_wz">'+ content +'</div>'
					        }
			titletipHtml +='</div>'
			titletipHtml +='</div>'
	
	
     $(addContainer).prepend(titletipHtml);
		$('body').on('tap','.er_top',function(){
			if(document.title == '报价结果'||document.title == '报价失败'||document.title == '核保失败'||document.title == '核保结果'){
				window.location.href='myself/order_list.html'
			}else{
				window.history.go(-1);
			}
		});
		$('body').on('tap','.er_btm',function(){
			if($(this).hasClass('er_btm_wz')){
				loginReg();//main.js
			}else{
				 window.location.href = "tel: "+ phoneNumber;
//				 console.log(phoneNumber)
			}
			
		})
		
}

	/**
 *   提示框
 * @param obj  obj类型为对象， 其属性有:
 * obj.title        弹窗的顶部的标题     默认为所需证件
 * obj.textContent  中间内容文字     
 * obj.addContarner 弹窗加在那个容器上   默认为body
 * obj.btnclose     关闭按钮
 * obj.sureFunction    回调函数
 * @constructor
 */
function TooltipMask(obj) {
    var title = ""
    var addContainer = "body";
    var _thiselent = "";
    var attrContarner = {
			item: []
		}
    var sureFunction = function () {
    }
    var backfun = function () {
    }
    if (arguments.length > 0) {
        if (typeof obj == "object") {
            title = obj.title || title;
            addContainer = obj.addContarner || addContainer;
            sureFunction = obj.sureFunction || sureFunction;
            attrContarner = obj.attrContarner || attrContarner;
            _thiselent = obj._thiselent || _thiselent;
            backfun = obj.backfun || backfun;
        } else {
            console.log("参数类型错误")
        }
    }
    var tooltipHtml = ''
	var tooltipHtml ='<div class="shade"></div><div class="mask_pull"><div class="mask_pull_tit flex"><span class="mask_pull_name">' + title + '</span>'+
					'<span class="pull-icon-close"><i class="iconfont icon-guanbi"></i></span></div>'+
					'<ul class="mask_pull_ul wrapul flex" data-length='+ attrContarner.item.length +'>'+
						'<script id="template" type="text/html">{{each item as value index}}<li data-code="{{value.code}}">'+
						'<a href="javascript:void(0)">{{value.carNumber}}</a>'+
						'<span class="correct iconfont icon-weibiaoti14"></span></li>'+
						'{{/each}}</script>'+
					'</ul>'+
				'</div>'
     $(addContainer).append(tooltipHtml);
     var html = template("template", attrContarner);
	 $('.mask_pull_ul').append(html);
	 $('.mask_pull_ul li').each(function(i){
		 $(this).on('tap',function(){
			$(this).addClass('mask_hover');
			$(this).siblings().removeClass('mask_hover');
			$(this).find('.correct').addClass('correct_hover');
			$(this).siblings().find('.correct').removeClass('correct_hover');
			$('.'+_thiselent).html($(this).find('a').html());
			$('.'+_thiselent).val($(this).find('a').html());
			$('.'+_thiselent).attr('data-code',$(this).attr('data-code'))
			$(".mask_pull").slideUp(200);
    		$('.shade').remove();
    		backfun();
		})
	});
}



	/**
 *   提示框
 * @param obj  obj类型为对象， 其属性有:
 * obj.title        弹窗的顶部的标题     默认为所需证件
 * obj.textContent  中间内容文字     
 * obj.addContarner 弹窗加在那个容器上   默认为body
 * obj.btnclose     关闭按钮
 * obj.sureFunction    回调函数
 * @constructor
 */
function TooltiploginCar(obj) {
     var title = ""
    var addContainer = "body";
    var _thiselent = "";
    var attrContarner = {
			item: []
		}
    var sureFunction = function () {
    }
    var backfun = function () {
    }
    if (arguments.length > 0) {
        if (typeof obj == "object") {
            title = obj.title || title;
            addContainer = obj.addContarner || addContainer;
            sureFunction = obj.sureFunction || sureFunction;
            attrContarner = obj.attrContarner || attrContarner;
            _thiselent = obj._thiselent || _thiselent;
            backfun = obj.backfun || backfun;
        } else {
            console.log("参数类型错误")
        }
    }
    var tooltipHtml = ''
	var tooltipHtml ='<div class="shade"></div><div class="mask_pull"><div class="mask_pull_tit flex"><span class="mask_pull_name">' + title + '</span>'+
					'<span class="pull-icon-close"><i class="iconfont icon-guanbi"></i></span></div>'+
					'<ul class="mask_pull_ul wrapul flex" data-length='+ attrContarner.item.length +'>'+
						'<script id="template" type="text/html">{{each item as value index}}<li data-code="{{value.code}}">'+
						'<a href="javascript:void(0)">{{value.carNumber}}</a>'+
						'<span class="correct iconfont icon-weibiaoti14"></span></li>'+
						'{{/each}}</script>'+
					'</ul>'+
				'</div>'
     $(addContainer).append(tooltipHtml);
     var html = template("template", attrContarner);
	 $('.mask_pull_ul').append(html);
	 $('.mask_pull_ul li').each(function(i){
		 $(this).on('tap',function(){
			$(this).addClass('mask_hover');
			$(this).siblings().removeClass('mask_hover');
			$(this).find('.correct').addClass('correct_hover');
			$(this).siblings().find('.correct').removeClass('correct_hover');
			$('.'+_thiselent).html($(this).find('a').html());
			$('.'+_thiselent).val($(this).find('a').html());
			$('.'+_thiselent).attr('data-code',$(this).attr('data-code'))
			$(".mask_pull").slideUp(200);
    		$('.shade').remove();
    		backfun();
		})
	});
}


//返回个人中心
	/**
 *   提示框
 * @param obj  obj类型为对象， 其属性有:
 * obj.title        弹窗的顶部的标题     默认为所需证件
 * obj.textContent  中间内容文字     
 * obj.addContarner 弹窗加在那个容器上   默认为body
 * obj.btnclose     关闭按钮
 * obj.sureFunction    回调函数
 * @constructor
 */
function myTooltip(obj) {
    var title = ""
    var addContainer = "body";
    var _thiselent = "";
    var attrContarner = {
			item: []
		}
    var sureFunction = function () {
    }
    if (arguments.length > 0) {
        if (typeof obj == "object") {
            title = obj.title || title;
            addContainer = obj.addContarner || addContainer;
            sureFunction = obj.sureFunction || sureFunction;
            attrContarner = obj.attrContarner || attrContarner;
            _thiselent = obj._thiselent || _thiselent;
        } else {
            console.log("参数类型错误")
        }
    }
    var tooltipHtml = ''
	var tooltipHtml ='<div class="shade1"></div><div class="mask_pull01"><div class="mask_pull_tit01 flex"style="padding-right:0rem" ><span class="mask_pull_name01">提示</span>'+
					'<span class="pull-icon-close" style="display: inline-block; padding:0"><i style="line-height: 0.8rem; padding: 0.2rem 0.2rem;" class="iconfont icon-guanbi"></i></span></div>'+
				'<div class="conmt">'+_thiselent+'</div><div class="maskBtn">返回个人中心</div></div>'
     $(addContainer).append(tooltipHtml);
	 $('.mask_pull_ul li').each(function(i){
		 $(this).on('tap',function(){
			
		})
		 
	});
	
	$('body').on('click','.mask_pull01 .pull-icon-close',function(){
	 	  $('.shade1').hide();
	 	  $('.mask_pull01').hide();
	});
	$('body').on('click','.maskBtn',function(){
		var urlLocation = getvalue8()[1] +'//'+ getvalue8()[2];
	 	window.location.href = urlLocation+'/page/myself/my.html'
	});
	
}