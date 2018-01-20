package com.xqh.financial.service;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.PayPXI;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.mapper.PayPXIMapper;
import com.xqh.financial.utils.*;
import com.xqh.financial.utils.xry.BeiBaoFuPay;
import com.xqh.financial.utils.xry.XRYPayEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2018/1/19.
 */
@Service
@Slf4j
public class XRYPayService
{

    @Resource
    private ConfigParamsUtils configParamsUtils;
    @Resource
    private BeiBaoFuPay beiBaoFuPay;
    @Resource
    private PayPXIMapper payPXIMapper;
    @Resource
    private XQHPayService xqhPayService;
    @Resource
    private PayOrderSerialMapper orderSerialMapper;

    public void pay(PayEntity payEntity, PayApp payApp, HttpServletRequest req, HttpServletResponse resp)
    {
        Search search = new Search();
        search.put("userId_eq", payApp.getUserId());
        List<PayPXI> pxiList = payPXIMapper.selectByExample(new ExampleBuilder(PayPXI.class).search(search).build());
        if(pxiList.size() != 1)
        {
            log.error("新瑞云 userId:{} 没有配置商编号", payApp.getUserId());
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
            return ;
        }

        XRYPayEntity xryParam = new XRYPayEntity();
        xryParam.setMoney(payEntity.getMoney());
        xryParam.setIp(CommonUtils.getIp(req));
        xryParam.setName(xqhPayService.getAppName(payApp.getAppName(), payEntity.getOrderSerial()));
        xryParam.setOrder(String.valueOf(payEntity.getOrderSerial()));
        xryParam.setCallbackUrl(configParamsUtils.getZpayNotifyHost() + "/xqh/financial/xry/callback");
        xryParam.setNotifyUrl(payApp.getNodifyUrl());
        xryParam.setKey(pxiList.get(0).getXryKey());
        xryParam.setParaId(pxiList.get(0).getXryParaId());
        xryParam.setAppId(pxiList.get(0).getXryAppId());

        String payUrl = beiBaoFuPay.wechatPay(xryParam);
        if(StringUtils.isNotBlank(payUrl)) {
            log.info("新瑞云通道 payUrl:{} payEntity:{}", payUrl, JSONObject.toJSON(payEntity));
            try
            {
                resp.sendRedirect(payUrl);
            } catch (IOException e)
            {
                log.error("新瑞云通道 跳转异常 payEntity:{}", JSONObject.toJSON(payEntity));
                xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
                return;
            }
        }
        else
        {
            log.error("新瑞云通道 获取payUrl失败 payUrl:{} payEntity:{}", payUrl, JSONObject.toJSON(payEntity));
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
            return;
        }
    }


    public PayOrderSerial verifyCallbackParam(Map<String, String> params)
    {

        // 取得订单流水
        PayOrderSerial orderSerial = null;
        String orderSerialIdStr = params.get("1516411243368");
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



}
