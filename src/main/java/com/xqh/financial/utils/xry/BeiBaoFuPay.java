package com.xqh.financial.utils.xry;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.utils.ConfigParamsUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class BeiBaoFuPay {

	@Resource
	private ConfigParamsUtils configParamsUtils;

	public static final String wechat_service_url = "http://pay.payfubao.com/sdk_transform/wx_wap_api";
	public static final String PARA_ID = "11241";
	public static final String APP_ID = "11292";
	public static final String PARA_KEY = "64a93a3fcbf3500352b7f885f0b3dbfb";

	public String wechatPay(String money,String type,String ip,String name,String order){

		String result = "";

		Map<String,String> param = new HashMap<>();
		param.put("body",name);
		param.put("total_fee",money);
		param.put("para_id",PARA_ID);
		param.put("app_id",APP_ID);
		param.put("order_no", order);
		param.put("notify_url", configParamsUtils.getZpayNotifyHost() + "/xry/callback");
		param.put("returnurl", configParamsUtils.getZpayNotifyHost() +  "/xry/notify");
		param.put("attach",creatOrderNumber());
		param.put("type",type);
		param.put("code","1");
		param.put("device_id","1");
		param.put("mch_create_ip",ip);
		param.put("mch_app_id","http://qianhaiyunji.com/");
		param.put("mch_app_name","云基网络");
		param.put("child_para_id","1_2");
		param.put("sign",creatSign(param, PARA_KEY));

		String post = HttpManager.post(wechat_service_url, param);
		JSONObject json = JSONObject.parseObject(post);
		System.out.println("onSuccess: "+post);

		if(json.getIntValue("status")==0){
			result = json.getString("pay_url");
		}else{
			result = "fail";
		}
		return result;
	}

	private static String creatSign(Map<String,String> map,String key){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(map.get("para_id")
				+map.get("app_id")
				+map.get("order_no")
				+map.get("total_fee")
				+key);
		String keyString = stringBuffer.toString();
		return DigestUtils.md5Hex(keyString.getBytes()).toLowerCase();
	}
	
	//public static void main(String[] args) {
	//	wechatPay("1", "2", "", "ceshi", creatOrderNumber());
	//}
	
	public static String creatOrderNumber() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		char c1 = (char) (int) (Math.random() * 26 + 97);
		char c2 = (char) (int) (Math.random() * 26 + 97);
		String paymentID = dateFormat.format(new Date(System.currentTimeMillis())).toString() + String.valueOf(c1) + String.valueOf(c2);
		return paymentID.toUpperCase();
	}
}
