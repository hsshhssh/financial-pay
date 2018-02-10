package com.xqh.financial.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.PayPJI;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.mapper.PayPJIMapper;
import com.xqh.financial.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hssh on 2018/2/10.
 */
@Service
@Slf4j
public class JFTPayService
{
    @Resource
    private PayPJIMapper payPJIMapper;
    @Resource
    private XQHPayService xqhPayService;
    @Resource
    private ConfigParamsUtils config;
    @Resource
    private PayOrderSerialMapper orderSerialMapper;;

    public void pay(PayEntity payEntity, PayApp payApp,  HttpServletRequest req, HttpServletResponse resp)
    {
        Search search = new Search();
        search.put("appId_eq", payEntity.getAppId());
        List<PayPJI> pjiList = payPJIMapper.selectByExample(new ExampleBuilder(PayPJI.class).search(search).build());
        if(pjiList.size() != 1)
        {
            log.error("金付通 配置有误 appId:{}", payEntity.getAppId());
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
            return ;
        }

        JSONObject json = new JSONObject();
        json.put("service", "create");
        json.put("trade_type", "pay.weixin.h5");
        json.put("mch_id", pjiList.get(0).getJftMchId());
        json.put("nonce_str", String.valueOf(System.currentTimeMillis()));
        json.put("body", payApp.getAppName());
        json.put("out_trade_no", String.valueOf(payEntity.getOrderSerial()));
        json.put("total_fee", String.valueOf(payEntity.getMoney()));
        json.put("device_info", "WEB");
        json.put("scene_info", "app_name=" + payApp.getAppName() + "&bundle_id=com.jinyou3.jinyou");
        json.put("mch_create_ip", CommonUtils.getIp(req));
        json.put("notify_url", config.getZpayNotifyHost() + "/xqh/financial/jft/callback");
        json.put("callback_url", payApp.getNodifyUrl());
        json.put("sign", getSign(json, pjiList.get(0).getJftKey()));

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
            try
            {
                resp.sendRedirect(payInfo.getString("mweb_url"));
            } catch (IOException e)
            {
                log.error("金付通 支付地址跳转失败 payEntity:{}, e:{}", JSONObject.toJSON(payEntity), Throwables.getStackTraceAsString(e));
                xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
            }
        } else {
            log.error("支付请求失败 响应值：{}", retJson);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);

        }

    }

    private String getSign(JSONObject json, String jftKey) {
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
        sb.append("key=" + jftKey);
        log.info("加密前字符串：{}", sb.toString());
        String sign = CommonUtils.getMd5(sb.toString()).toUpperCase();
        log.info("加密结果：{}", sign);
        return sign;
    }

    public PayOrderSerial verifyCallbackParam(JSONObject params)
    {
        if(!"0".equals(params.getString("status")) || !"0".equals(params.getString("result_code")))
        {
            log.error("金付通支付 异步回调 支付结果不为成功 params:{}", params);
            return null;
        }

        // 取得订单流水
        PayOrderSerial orderSerial = null;
        String orderSerialId = params.getString("out_trade_no");
        if(StringUtils.isNotBlank(orderSerialId) && StringUtils.isNumeric(orderSerialId))
        {
            orderSerial = orderSerialMapper.selectByPrimaryKey(Integer.valueOf(orderSerialId));
        }
        if(null == orderSerial)
        {
            log.error("金付通支付 异步回调 订单流水号异常 params：{}", params);
            return null;
        }

        return orderSerial;
    }


    public void sendResult(HttpServletResponse resp, String key)
    {
        JSONObject res = new JSONObject();
        res.put("status", "0");
        res.put("message", "success");
        res.put("sign", getSign(res, key));
        log.info("回调响应：{}", res);
        CommonUtils.writeResponse(resp, res);
    }
}
