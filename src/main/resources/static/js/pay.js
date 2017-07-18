var store=window.sessionStorage;
store.payway='002';

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
$('#ali').click(function(){//支付宝支付方式的选择
	
 $('#wx').attr('src', '/img/unselect.png');
  $('#ali').attr('src', '/img/select.png');
  store.payway='002';
});

$('#surepay').click(function(){//支付宝支付方式的选择
	if(store.payway=='001')
	{
		location.href='http://139.196.51.152:8080/checkstand/pay?payUserId=2&appId=8&money=1&sign=0059743044813112892732a13b788109&payType=1';
		
	}
		if(store.payway=='002')
	{
		location.href='http://139.196.51.152:8080/checkstand/pay?payUserId=2&appId=8&money=1&sign=0059743044813112892732a13b788109&payType=2';
		
	}
 
});
 
 function getUrlParam(name) {//获取钱数并填充。
 		
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            //返回参数值
            if (r != null){
            $('#money').html(unescape(r[2]));
            return unescape(r[2]);
            }
            
            return null; 
      }
$(document).ready(function(){ //页面加载完成后执行的函数。
		
		getUrlParam('money');
}); 