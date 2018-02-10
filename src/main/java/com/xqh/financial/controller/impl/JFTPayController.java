package com.xqh.financial.controller.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.xqh.financial.controller.api.IJFTPayController;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.service.JFTPayService;
import com.xqh.financial.service.XQHPayAsyncService;
import com.xqh.financial.service.XQHPayTxService;
import com.xqh.financial.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;

/**
 * Created by hssh on 2018/2/10.
 */
@RestController
@Slf4j
public class JFTPayController implements IJFTPayController
{
    @Resource
    private XQHPayTxService xqhPayTxService;
    @Resource
    private XQHPayAsyncService xqhPayAsyncService;
    @Resource
    private JFTPayService jftPayService;

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("金付通异步回调 params:{}", JSONObject.toJSON(params));

        PayOrderSerial orderSerial = jftPayService.verifyCallbackParam(params);
        if(null == orderSerial)
        {
            log.error("金付通异步回调 校验不通过 params:{}", JSONObject.toJSON(params));
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }

        // 新增订单和回调记录
        CallbackEntity callbackEntity = null;
        try
        {
            callbackEntity = xqhPayTxService.insertOrderAndGenCallbackEntity(orderSerial, params.get("wxno"), "金付通支付通道");
        } catch (DuplicateKeyException e)
        {
            // 重复回调 => 返回成功
            log.warn("金付通支付通道 异步回调 重复回调 orderSerial:{} param:{} e:{}", orderSerial, params, Throwables.getStackTraceAsString(e));
            CommonUtils.writeResponse(resp, "ok");
            return ;
        }
        if(null == callbackEntity)
        {
            log.error("金付通支付通道 异步回调 插入订单失败 param:{}", params);
            CommonUtils.writeResponse(resp, "fail");
            return;
        }
        //返回成功
        log.warn("金付通支付通道 异步回调 回调成功 orderSerial:{} param:{}", orderSerial, params);
        CommonUtils.writeResponse(resp, "ok");


        // 回调商户
        xqhPayAsyncService.callbackUser(callbackEntity, "金付通支付通道");
    }
}
