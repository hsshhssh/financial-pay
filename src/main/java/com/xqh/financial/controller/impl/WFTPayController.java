package com.xqh.financial.controller.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.xqh.financial.controller.api.IPayWFTPayController;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.service.WFTPayService;
import com.xqh.financial.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;

import static com.xqh.financial.utils.jobs.CallbackJobs.logger;

/**
 * Created by hssh on 2018/1/14.
 */
@RestController
@Slf4j
public class WFTPayController implements IPayWFTPayController
{

    @Resource
    private WFTPayService wftPayService;

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("威富通异步回调 params:{}", JSONObject.toJSON(params));

        PayOrderSerial orderSerial = wftPayService.verifyCallbackParam(params);
        if(null == orderSerial)
        {
            log.error("威富通异步回调 校验不通过 params:{}", JSONObject.toJSON(params));
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }


        // 插入订单
        CallbackEntity callbackEntity = null;
        try
        {
            callbackEntity = wftPayService.insertOrderAndGenCallbackEntity(orderSerial, params.get("transaction_id"));
        } catch (DuplicateKeyException e)
        {
            // 重复回调 => 返回成功
            logger.warn("威富通支付 异步回调 重复回调 orderSerial:{} param:{} e:{}", orderSerial, params, Throwables.getStackTraceAsString(e));
            CommonUtils.writeResponse(resp, "success");
            return ;
        }
        if(null == callbackEntity)
        {
            logger.error("威富通支付 异步回调 插入订单失败 param:{}", params);
            CommonUtils.writeResponse(resp, "fail");
            return;
        }

        // 返回成功
        logger.info("威富通支付 异步回调成功 返回SUCCESS 正在异步回调商户 appId:{}", orderSerial.getAppId());
        CommonUtils.writeResponse(resp, "success");

        // 回调并修改订单数据
        wftPayService.callbackUser(callbackEntity);

    }
}
