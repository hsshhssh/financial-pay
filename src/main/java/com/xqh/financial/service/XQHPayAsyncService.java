package com.xqh.financial.service;

import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * Created by hssh on 2018/1/20.
 */
@Service
@Slf4j
public class XQHPayAsyncService
{
    @Resource
    private XQHPayService xqhPayService;

    /**
     * 异步回调商户
     */
    @Async
    public void callbackUser(CallbackEntity callbackEntity, String payChannelName)
    {
        log.info("{} 异步回调商户开始 orderId:{} appId:{}", payChannelName, callbackEntity.getOrderId(), callbackEntity.getAppId());
        String url = xqhPayService.genCallbackUrl(callbackEntity);
        log.info("{} 回调商户url:{}", payChannelName,  url);

        HttpResult httpResult = HttpUtils.get(url);

        log.info("{} 回调商户返回值: {}", payChannelName,  httpResult);

        // 根据回调结果修改订单状态
        xqhPayService.updateOrderStatus(httpResult, callbackEntity.getOrderId(), callbackEntity.getCfrId());

        log.info("{} 回调商户异步操作结束 orderId:{}", payChannelName,  callbackEntity.getOrderId());

    }

}
