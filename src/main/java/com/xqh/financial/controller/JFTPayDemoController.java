package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hssh on 2018/2/4.
 */
@RestController
@RequestMapping("jft")
@Slf4j
public class JFTPayDemoController
{
    @Resource
    private ConfigParamsUtils config;

    @GetMapping("pay")
    public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        JSONObject json = new JSONObject();
        json.put("service", "create");
        json.put("trade_type", "pay.weixin.h5");
        json.put("mch_id", config.getJftMchId());
        json.put("nonce_str", String.valueOf(System.currentTimeMillis()));
        json.put("body", "测试应用");
        json.put("out_trade_no", String.valueOf(System.currentTimeMillis()));
        json.put("total_fee", String.valueOf(money));
        json.put("device_info", "WEB");
        json.put("scene_info", "app_name=app&bundle_id=com.jinyou3.jinyou");
        json.put("mch_create_ip", CommonUtils.getIp(req));
        json.put("notify_url", config.getZpayNotifyHost() + "/jft/callback");
        json.put("callback_url", config.getZpayNotifyHost() + "/jft/notify");
        json.put("sign", getSign(json));

        Header[] headers = { new BasicHeader("Content-Type", "application/json") };
        String string = json.toString();
        StringEntity entity = new StringEntity(string, "utf-8");
        log.info("请求参数：{}", string);
        HttpResult result = HttpUtils.post("https://pay.echase.cn/jygateway/api", headers, entity);
        log.info("响应值：{}", result);

        JSONObject retJson = JSONObject.parseObject(result.getContent());
        if("0".equals(retJson.getString("status")) && "0".equals(retJson.getString("result_code"))) {
            JSONObject payInfo = retJson.getJSONObject("pay_info");
            log.info("支付请求成功 payInfo:{}", payInfo);
            resp.sendRedirect(payInfo.getString("mweb_url"));
        } else {
            log.error("支付请求失败 响应值：{}", retJson);
        }
    }

    @GetMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("金富通 异步回调参数：{}", JSONObject.toJSON(params));
        JSONObject res = new JSONObject();
        res.put("status", "0");
        res.put("message", "success");
        res.put("sign", getSign(res));
        log.info("回调响应：{}", res);
        CommonUtils.writeResponse(resp, res);
    }

    @GetMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("金富通 跳转页面参数：{}", JSONObject.toJSON(params));
    }

    private String getSign(JSONObject json) {
        Map<String, String> map = new TreeMap<>();
        for (String s : json.keySet())
        {
            map.put(s, json.getString(s));
        }

        StringBuffer sb = new StringBuffer();
        for (String key : map.keySet())
        {
            sb.append(key);
            sb.append("=");
            sb.append(json.getString(key));
            sb.append("&");
        }
        sb.append("key=" + config.getJftKey());
        log.info("加密前字符串：{}", sb.toString());
        String sign = CommonUtils.getMd5(sb.toString()).toUpperCase();
        log.info("加密结果：{}", sign);
        return sign;
    }

}
