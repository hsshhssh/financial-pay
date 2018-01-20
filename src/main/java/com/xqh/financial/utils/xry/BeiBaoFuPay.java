package com.xqh.financial.utils.xry;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.utils.ConfigParamsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class BeiBaoFuPay {

	@Resource
	private ConfigParamsUtils configParamsUtils;

	public static final String wechat_service_url = "http://pay.payfubao.com/sdk_transform/wx_wap_api";
	public static final String PARA_ID = "11241";
	public static final String APP_ID = "11292";
	public static final String PARA_KEY = "64a93a3fcbf3500352b7f885f0b3dbfb";

	public String wechatPay(XRYPayEntity entity){

		String result = "";

		Map<String,String> param = new HashMap<>();
		param.put("body", entity.getName());
		param.put("total_fee", String.valueOf(entity.getMoney()));
		param.put("para_id", entity.getParaId());
		param.put("app_id", entity.getAppId());
		param.put("order_no", entity.getOrder());
		param.put("notify_url", entity.getCallbackUrl());
		param.put("returnurl", entity.getNotifyUrl());
		param.put("attach",creatOrderNumber());
		param.put("type","2");
		param.put("code","1");
		param.put("device_id","1");
		param.put("mch_create_ip", entity.getIp());
		param.put("mch_app_id","http://qianhaiyunji.com/");
		param.put("mch_app_name","云基网络");
		param.put("child_para_id","1_2");
		param.put("sign",creatSign(param, entity.getKey()));
		log.info("新瑞云 支付参数：{}", JSONObject.toJSON(param));
		String post = HttpManager.post(wechat_service_url, param);
		log.info("新瑞云 支付返回值：{}", post);
		JSONObject json = JSONObject.parseObject(post);

		if(json.getIntValue("status")==0){
			result = json.getString("pay_url");
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
