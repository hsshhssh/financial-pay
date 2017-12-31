package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.HttpsUtils;
import com.xqh.financial.utils.wft.MD5;
import com.xqh.financial.utils.wft.SignUtils;
import com.xqh.financial.utils.wft.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.StringEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Created by hssh on 2017/12/28.
 */
@RestController
@RequestMapping("wft")
@Slf4j
public class WFTPayDemoController
{

    @Resource
    private ConfigParamsUtils config;

    @GetMapping("pay")
    public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        SortedMap<String,String> map = new TreeMap<>();

        map.put("service", "pay.weixin.wappay");
        map.put("mch_id", "175510359638");
        map.put("out_trade_no", System.currentTimeMillis() + CommonUtils.generateRandom(5));
        map.put("body", "商品描述");
        map.put("total_fee", String.valueOf(money));
        map.put("mch_create_ip", CommonUtils.getIp(req));
        map.put("notify_url", config.getZpayNotifyHost() + "/wft/callback");
        map.put("callback_url", config.getZpayNotifyHost() + "/wft/notify");
        map.put("device_info", "iOS_WAP");
        map.put("mch_app_name", "百度");
        map.put("mch_app_id", "www.baidu.com");
        map.put("nonce_str", CommonUtils.generateRandom(15));
        Map<String,String> params = SignUtils.paraFilter(map);
        StringBuilder buf = new StringBuilder((params.size() +1) * 10);
        SignUtils.buildPayParams(buf,params,false);
        String preStr = buf.toString();
        String sign = MD5.sign(preStr, "&key=" + "61307e5f2aebcacecbcca6fe5296df9c", "UTF-8");
        map.put("sign", sign);
        String xmlStr = XmlUtils.parseXML(map);
        log.info("pay params:{}", xmlStr);

        StringEntity stringEntity = new StringEntity(xmlStr, "UTF-8");
        HttpResult httpResult = HttpsUtils.post("https://pay.swiftpass.cn/pay/gateway", null, stringEntity, "UTF-8");
        log.info("pay result:{}", httpResult);

        if(200 == httpResult.getStatus()) {
            Map<String, String> resultMap = XmlUtils.toMap(httpResult.getContent().getBytes(), "UTF-8");
            if("0".equals(resultMap.get("status")) && "0".equals(resultMap.get("result_code"))) {
                String pay_info = resultMap.get("pay_info");
                log.info("payurl:{}", pay_info);
                resp.sendRedirect(pay_info);
            }
        }

        CommonUtils.writeResponse(resp, "pay fail");
    }

    @PostMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("callback params:{}", JSONObject.toJSON(params));

        CommonUtils.writeResponse(resp, "success");
        return;
    }

    @GetMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp) {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("notify params:{}", JSONObject.toJSON(params));

        CommonUtils.writeResponse(resp, params);
        return;
    }

}
