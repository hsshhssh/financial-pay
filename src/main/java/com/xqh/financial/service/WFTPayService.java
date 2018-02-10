package com.xqh.financial.service;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.entity.*;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.entity.other.PayInfoEntity;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.mapper.PayPWIMapper;
import com.xqh.financial.utils.*;
import com.xqh.financial.utils.wft.MD5;
import com.xqh.financial.utils.wft.SignUtils;
import com.xqh.financial.utils.wft.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.StringEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.xqh.financial.utils.jobs.CallbackJobs.logger;

/**
 * Created by hssh on 2018/1/14.
 */
@Service
@Slf4j
public class WFTPayService
{
    @Resource
    private XQHPayService xqhPayService;
    @Resource
    private PayPWIMapper pwiMapper;
    @Resource
    private ConfigParamsUtils config;
    @Resource
    private PayOrderSerialMapper orderSerialMapper;
    @Resource
    private PayAppMapper payAppMapper;

    public void pay(HttpServletRequest req, HttpServletResponse resp, PayEntity payEntity, PayApp payApp)
    {
        //获取配置信息
        Search search = new Search();
        search.put("userId_eq", payEntity.getUserId());
        List<PayPWI> infoList = pwiMapper.selectByExample(new ExampleBuilder(PayPWI.class).search(search).build());
        if(CollectionUtils.isEmpty(infoList))
        {
            log.error("没有配置威富通商编号 userId:{}", payEntity.getUserId());
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
            return ;
        }

        if(Constant.WXWAP_PAY_TYPE == payEntity.getPayType())
        {
            log.info("威富通支付 微信wap支付 appId:{} orderSerial:{}", payApp.getId(), payEntity.getOrderSerial());
        }
        else if(Constant.WX_OFFICE_ACCOUNT_PAY_TYPE == payEntity.getPayType())
        {
            log.info("威富通支付 公众号支付 appId:{} orderSerial:{}", payApp.getId(), payEntity.getOrderSerial());
            dealWithWxOffice(infoList.get(0), payEntity, CommonUtils.getIp(req), payApp, resp);
        }
        else
        {
            log.error("威富通为 无支付方式 payEntity:{}", JSONObject.toJSON(payEntity));
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
        }

    }

    /**
     * 公众号支付
     */
    private void dealWithWxOffice(PayPWI payPWI, PayEntity payEntity, String ip, PayApp payApp, HttpServletResponse resp)
    {
        try
        {
            SortedMap<String,String> map = new TreeMap<>();

            map.put("service", "pay.weixin.jspay");
            map.put("mch_id", payPWI.getWftMchId());
            map.put("is_raw", "1");
            map.put("out_trade_no", String.valueOf(payEntity.getOrderSerial()));
            map.put("body", payApp.getAppName() + xqhPayService.getAppNameSuffix(payEntity.getOrderSerial()));
            map.put("sub_openid", payEntity.getOpenId());
            map.put("sub_appid", payPWI.getWxAppId());
            map.put("total_fee", String.valueOf(payEntity.getMoney()));
            map.put("mch_create_ip", ip);
            map.put("notify_url", config.getZpayNotifyHost() + "/xqh/financial/wft/pay/callback");
            map.put("nonce_str", CommonUtils.generateRandom(15));
            Map<String,String> params = SignUtils.paraFilter(map);
            StringBuilder buf = new StringBuilder((params.size() +1) * 10);
            SignUtils.buildPayParams(buf,params,false);
            String preStr = buf.toString();
            String sign = MD5.sign(preStr, "&key=" + payPWI.getWftKey(), "UTF-8");
            map.put("sign", sign);
            String xmlStr = XmlUtils.parseXML(map);
            log.info("威富通公众号支付 请求参数params:{}", xmlStr);

            StringEntity stringEntity = new StringEntity(xmlStr, "UTF-8");
            HttpResult httpResult = HttpsUtils.post("https://pay.swiftpass.cn/pay/gateway", null, stringEntity, "UTF-8");
            log.info("威富通公众号支付 请求返回值result:{}", httpResult);

            if(200 == httpResult.getStatus()) {
                Map<String, String> resultMap = XmlUtils.toMap(httpResult.getContent().getBytes(), "UTF-8");
                log.info("威富通公众号支付返回值map:{} ", JSONObject.toJSON(resultMap));
                if("0".equals(resultMap.get("status")) && "0".equals(resultMap.get("result_code"))) {
                    log.info("pay_info:{} ， token_id:{}",resultMap.get("pay_info"), resultMap.get("token_id"));
                    PayInfoEntity payInfoEntity = new PayInfoEntity();
                    payInfoEntity.setPayType(Constant.WX_OFFICE_ACCOUNT_PAY_TYPE);
                    payInfoEntity.setRetCode(Constant.PAYINFO_SUCC_RETCODE);
                    payInfoEntity.setPayInfo(JSONObject.parseObject(resultMap.get("pay_info")));

                    resp.getWriter().print(JSONObject.toJSONString(payInfoEntity));
                    return;
                }
            }
            else
            {
                log.error("威富通公众号支付 调用支付请求失败 payEntity:{}", payEntity);
                xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
                return ;
            }
        }
        catch (Exception e)
        {
            log.error("威富通公众号支付 异常payEntity:{}", payEntity, e);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
            return ;
        }
    }


    /**
     * 校验回调参数
     */
    public PayOrderSerial verifyCallbackParam(TreeMap<String, String> params)
    {
        //TODO 检验签名

        // 支付结果
        if(!"0".equals(params.get("status")) || !"0".equals(params.get("result_code")))
        {
            log.error("威富通支付 异步回调支付状态不为成功 params:{}", JSONObject.toJSON(params));
            return null;
        }

        // 取得订单流水
        PayOrderSerial orderSerial = null;
        String orderSerialIdStr = params.get("out_trade_no");
        if(orderSerialIdStr != null && StringUtils.isNumeric(orderSerialIdStr))
        {
            orderSerial = orderSerialMapper.selectByPrimaryKey(Integer.valueOf(orderSerialIdStr));
        }
        if(null == orderSerial)
        {
            log.error("威富通支付 异步回调 订单流水号异常 params：{}", JSONObject.toJSON(params));
            return null;
        }

        return orderSerial;
    }

    /**
     *
     */
    @Transactional
    public CallbackEntity insertOrderAndGenCallbackEntity(PayOrderSerial orderSerial, String platformOrderNo)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        // 取得payApp
        PayApp payApp = payAppMapper.selectByPrimaryKey(orderSerial.getAppId());
        if(null == payApp)
        {
            logger.error("威富通支付 异步回调 获得应用信息失败 appId:{} orderSerialId:{}", orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        logger.info("威富通支付 异步回调 取得应用信息成功 appId:{}", payApp.getId());


        // 生成回调实体类
        CallbackEntity callbackEntity = xqhPayService.getCallbackByOrderSerial(orderSerial, payApp.getSecretkey());
        logger.info("威富通支付 异步回调 生成回调实体类成功 callbackEntity:{}", callbackEntity);


        // 创建订单
        PayOrder payOrder = xqhPayService.insertOrderByOrderSerial(orderSerial, callbackEntity.getOrderNo(), platformOrderNo);
        if(null == payOrder)
        {
            logger.error("威富通支付 异步回调 创建订单失败 appId:{} orderSerialId:{}", orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        callbackEntity.setOrderId(payOrder.getId());
        callbackEntity.setCallbackUrl(payApp.getCallbackUrl());
        logger.info("威富通支付 异步回调 创建订单成功 appId:{}", payApp.getId());


        // 创建失败回调记录
        PayCFR payCFR = xqhPayService.insertCFR(orderSerial, callbackEntity, payOrder);
        callbackEntity.setCfrId(payCFR.getId());
        logger.info("威富通支付 异步回调 创建失败的回调记录成功 appId:{}", payApp.getId());

        return callbackEntity;
    }

    /**
     * 异步回调商户
     */
    @Async
    public void callbackUser(CallbackEntity callbackEntity)
    {
        logger.info("威富通支付 异步回调商户开始 orderId:{} appId:{}", callbackEntity.getOrderId(), callbackEntity.getAppId());
        String url = xqhPayService.genCallbackUrl(callbackEntity);
        logger.info("威富通支付 回调商户url:{}", url);

        HttpResult httpResult = HttpUtils.get(url);

        logger.info("威富通支付 回调商户返回值: {}", httpResult);

        // 根据回调结果修改订单状态
        xqhPayService.updateOrderStatus(httpResult, callbackEntity.getOrderId(), callbackEntity.getCfrId());

        logger.info("威富通支付 回调商户异步操作结束 orderId:{}", callbackEntity.getOrderId());

    }

}
