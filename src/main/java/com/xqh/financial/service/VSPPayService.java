package com.xqh.financial.service;

import com.google.common.base.VerifyException;
import com.google.common.collect.Maps;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayCFR;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.Constant;
import com.xqh.financial.utils.HttpUtils;
import com.xqh.financial.utils.vsp.HttpConnectionUtil;
import com.xqh.financial.utils.vsp.SybUtil;
import com.xqh.financial.utils.vsp.VSPConfigParamUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 通联支付平台
 * Created by hssh on 2017/7/2.
 */
@Service
public class VSPPayService
{
    private static Logger logger = LoggerFactory.getLogger(VSPPayService.class);

    @Autowired
    private VSPConfigParamUtils vspConfig;

    @Autowired
    private ConfigParamsUtils config;

    @Autowired
    private PayOrderSerialMapper orderSerialMapper;

    @Autowired
    private XQHPayService xqhPayService;

    @Autowired
    private PayAppMapper payAppMapper;

    /**
     * 通联支付接口
     */
    public void pay(HttpServletResponse resp, int userId, int appId, int money, int orderSerial, int payType, PayApp payApp)
    {
        // 获得支付方式
        String vspPayType = getPayType(payType);
        if(null == vspPayType)
        {
            logger.error("通联支付 无支付通道 appId:{} userId:{} payType:{}", appId, userId, payType);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(),  Constant.RESULT_NO_PAYTYPE);
        }


        // 获得支付url
        String payUrl = getPayUrl(money, payApp.getAppName(), orderSerial, vspPayType);
        if(null == payUrl)
        {
            logger.error("通联支付生成支付url失败 appId:{} orderSerial:{}", appId, orderSerial);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
        }

        logger.info("通联支付发起支付 url:{}", payUrl);

        try
        {
            resp.sendRedirect(payUrl);
        }
        catch (IOException e)
        {
            logger.error("通联支付发起支付失败 appId:{} orderSerial:{} e:{}", appId, orderSerial, e);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
        }

    }


    /**
     * 校验回调参数
     */
    public PayOrderSerial verifyCallbackParam(TreeMap<String, String> params)
    {
        // 判断订单流水号
        PayOrderSerial orderSerial = null;
        String cusorderid = params.get("cusorderid");
        if(cusorderid != null && StringUtils.isNumeric(cusorderid))
        {
            orderSerial = orderSerialMapper.selectByPrimaryKey(Integer.valueOf(cusorderid));
        }

        if(orderSerial == null)
        {
            logger.error("通联支付回调 订单流水号无效 orderSerial:{}", cusorderid);
            throw new VerifyException(String.format("通联支付回调 订单流水号无效 orderSerial:%s", cusorderid));
        }

        // 支付结果
        String trxstatus = params.get("trxstatus");
        if(null == trxstatus || !"0000".equals(trxstatus))
        {
            logger.error("通联支付回调 状态非成功 trxstatus:{}", trxstatus);
            throw new VerifyException("通联支付回调 状态非成功");
        }

        // 判断通联参数
        String vspAppId = params.get("appid");
        String vspCusid = params.get("cusid");
        if(!vspConfig.getAppid().equals(vspAppId) || !vspConfig.getCusid().equals(vspCusid))
        {
            logger.error("通联支付回调 通联支付用户id或者应用id无效 pararm:{}", params);
            throw new VerifyException("通联支付回调 通联支付用户id或者应用id无效");
        }

        // 校验金额
        String money = params.get("trxamt");
        if(null == money || !StringUtils.isNumeric(money) || orderSerial.getMoney().intValue() != Integer.valueOf(money).intValue())
        {
            logger.error("通联支付回调 金额无效 orderSerialMoney:{}, callbackMoney:{}", orderSerial.getMoney(), money);
            throw new VerifyException("通联支付回调 金额无效");
        }


        //TODO 校验签名

        return orderSerial;

    }


    /**
     * 创建订单
     */
    @Transactional
    public CallbackEntity insertOrderAndGenCallbackEntity(PayOrderSerial orderSerial, TreeMap<String, String> params)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        // 取得payApp
        PayApp payApp = payAppMapper.selectByPrimaryKey(orderSerial.getAppId());
        if(null == payApp)
        {
            logger.error("通联支付回调 获得应用信息失败 appId:{} orderSerialId:{}", orderSerial.getAppId(), orderSerial.getId());
            return null;
        }

        // 生成回调实体类
        CallbackEntity callbackEntity = xqhPayService.getCallbackByOrderSerial(orderSerial, payApp.getSecretkey());

        // 创建订单
        PayOrder payOrder = xqhPayService.insertOrderByOrderSerial(orderSerial, callbackEntity.getOrderNo(), params.get("trxid"));
        if(null == payOrder)
        {
            logger.error("通联支付回调 创建订单失败 appId:{} orderSerialId:{}", orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        callbackEntity.setOrderId(payOrder.getId());
        callbackEntity.setCallbackUrl(payApp.getCallbackUrl());

        // 创建失败回调记录
        PayCFR payCFR = xqhPayService.insertCFR(orderSerial, callbackEntity, payOrder);
        callbackEntity.setCfrId(payCFR.getId());

        return callbackEntity;

    }

    /**
     * 异步回调商户
     */
    @Async
    public void callbackUser(CallbackEntity callbackEntity)
    {
        logger.info("异步回调商户开始 orderId:{} appId:{}", callbackEntity.getOrderId(), callbackEntity.getAppId());
        String url = xqhPayService.genCallbackUrl(callbackEntity);
        logger.info("回调商户url:{}", url);

        HttpResult httpResult = HttpUtils.get(url);

        logger.info("回调商户返回值: {}", httpResult);

        // 根据回调结果修改订单状态
        xqhPayService.updateOrderStatus(httpResult, callbackEntity.getOrderId(), callbackEntity.getCfrId());

        logger.info("回调商户异步操作结束 orderId:{}", callbackEntity.getOrderId());

    }

    public String getPayUrl(int money, String appName, int orderSerial, String payType)
    {
        try
        {
            HttpConnectionUtil http = new HttpConnectionUtil("https://vsp.allinpay.com/apiweb/unitorder"+"/pay");
            http.init();
            TreeMap<String, String> params = Maps.newTreeMap();

            params.put("cusid", vspConfig.getCusid().trim());
            params.put("appid", vspConfig.getAppid().trim());
            params.put("version", "11");
            params.put("trxamt", String.valueOf(money));
            params.put("reqsn", String.valueOf(orderSerial));
            params.put("paytype", payType);
            params.put("randomstr", SybUtil.getValidatecode(8));
            params.put("body", appName);
            //params.put("remark", "mark");
            //params.put("acct", acct);
            params.put("notify_url", config.getZpayNotifyHost().trim() + "/xqh/financial/vsp/pay/callback");
            params.put("sign", SybUtil.sign(params,vspConfig.getKey().trim()));

            byte[] bys = http.postParams(params, true);
            String result = new String(bys,"UTF-8");
            Map<String,String> map = SybUtil.handleResult(result);
            return map.get("payinfo");

        }
        catch (Exception e)
        {
            logger.error("通联支付生成支付url异常 e:{}", e);
            return null;
        }
    }

    public String getPayType(int payType)
    {
        if(payType == Constant.ALIPAYWAP_PAY_TYPE)
        {
            // 支付宝
            return "A01";
        }
        else
        {
            return null;
        }
    }

}
