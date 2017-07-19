var store=window.sessionStorage;
store.payway='001';

$('#wx').click(function(){//微信支付方式的选择
	
 $('#wx').attr('src', '/img/select.png');
  $('#ali').attr('src', '/img/unselect.png');
  store.payway='001';
});

$('#ali').click(function(){//支付宝支付方式的选择
	
 $('#wx').attr('src', '/img/unselect.png');
  $('#ali').attr('src', '/img/select.png');
  store.payway='002';
});



 
 function getUrlParam(name) {//获取钱数并填充。
 		
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            //返回参数值
            if (r != null){
            
            return unescape(r[2]);
            }
            
            return null; 
      }
 $('#surepay').click(function(){//确认支付
	var payUserId=getUrlParam('payUserId');
	var appId=getUrlParam('appId');
	var sign=getUrlParam('sign');
	var money=getUrlParam('money');
	if(store.payway=='001')
	{
		location.href='http://139.196.51.152:8080/checkstand/pay?payUserId='+payUserId+'&appId='+appId+'&money='+money+'&sign='+sign+'&payType=1';
		
	}
		if(store.payway=='002')
	{
		location.href='http://139.196.51.152:8080/checkstand/pay?payUserId='+payUserId+'&appId='+appId+'&money='+money+'&sign='+sign+'&payType=2';
		
	}
 
});
$(document).ready(function(){ //页面加载完成后执行的函数。
	
		$('#money').html(getUrlParam('money')/100);
		
}); 