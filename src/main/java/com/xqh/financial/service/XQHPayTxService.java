package com.xqh.financial.service;

import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayCFR;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.mapper.PayAppMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.xqh.financial.utils.jobs.CallbackJobs.logger;

/**
 * Created by hssh on 2018/1/20.
 */
@Service
@Slf4j
public class XQHPayTxService
{
    @Resource
    private PayAppMapper payAppMapper;
    @Resource
    private XQHPayService xqhPayService;

    /**
     * 新增订单和回调记录
     */
    @Transactional
    public CallbackEntity insertOrderAndGenCallbackEntity(PayOrderSerial orderSerial, String platformOrderNo, String payChannelName)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        // 取得payApp
        PayApp payApp = payAppMapper.selectByPrimaryKey(orderSerial.getAppId());
        if(null == payApp)
        {
            log.error("{} 异步回调 获得应用信息失败 appId:{} orderSerialId:{}", payChannelName, orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        log.info("{} 异步回调 取得应用信息成功 appId:{}", payChannelName, payApp.getId());


        // 生成回调实体类
        CallbackEntity callbackEntity = xqhPayService.getCallbackByOrderSerial(orderSerial, payApp.getSecretkey());
        log.info("{} 异步回调 生成回调实体类成功 callbackEntity:{}", payChannelName, callbackEntity);


        // 创建订单
        PayOrder payOrder = xqhPayService.insertOrderByOrderSerial(orderSerial, callbackEntity.getOrderNo(), platformOrderNo);
        if(null == payOrder)
        {
            log.error("{} 异步回调 创建订单失败 appId:{} orderSerialId:{}", payChannelName, orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        callbackEntity.setOrderId(payOrder.getId());
        callbackEntity.setCallbackUrl(payApp.getCallbackUrl());
        log.info("{} 异步回调 创建订单成功 appId:{}", payChannelName, payApp.getId());


        // 创建失败回调记录
        PayCFR payCFR = xqhPayService.insertCFR(orderSerial, callbackEntity, payOrder);
        callbackEntity.setCfrId(payCFR.getId());
        log.info("{} 异步回调 创建失败的回调记录成功 appId:{}", payChannelName, payApp.getId());

        return callbackEntity;
    }

}
