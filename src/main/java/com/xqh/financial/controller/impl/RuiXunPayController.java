package com.xqh.financial.controller.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.xqh.financial.controller.api.IRuiXunPayController;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.service.RuiXunPayService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ruixun.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;

/**
 * Created by hssh on 2017/7/11.
 */
@RestController
public class RuiXunPayController implements IRuiXunPayController
{
    private static Logger logger = LoggerFactory.getLogger(RuiXunPayController.class);

    @Autowired
    private RuiXunPayService ruiXunPayService;

    @Autowired
    private CertificateUtils certificateUtils;

    @Override
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        logger.info("锐讯支付 返回地址 参数：{}", JSONObject.toJSON(params));
    }

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        // 取得请求参数
        TreeMap<String, String> params = CommonUtils.getParams(req);
        logger.info("锐讯支付 异步回调地址 参数:{}", JSONObject.toJSON(params));

        // 检验参数
        PayOrderSerial orderSerial = null;
        try
        {
            orderSerial = ruiXunPayService.verifyCallbackParam(params);
        }
        catch (ValidationException e)
        {
            logger.error("锐讯支付 异步回调 参数检验失败 msg:{} params:{}", e.getMessage(), params);
            CommonUtils.writeResponse(resp, "FAIL");
            return;
        }


        // 插入订单
        CallbackEntity callbackEntity = null;
        try
        {
            callbackEntity = ruiXunPayService.insertOrderAndGenCallbackEntity(orderSerial, params);
        } catch (DuplicateKeyException e)
        {
            // 重复回调 => 返回成功
            logger.warn("锐讯支付 异步回调 重复回调 orderSerial:{} param:{} e:{}", orderSerial, params, Throwables.getStackTraceAsString(e));
            CommonUtils.writeResponse(resp, "SUCCESS");
            return ;
        }
        if(null == callbackEntity)
        {
            logger.error("锐讯支付 异步回调 插入订单失败 param:{}", params);
            CommonUtils.writeResponse(resp, "FAIL");
            return;
        }

        // 返回成功
        logger.info("锐讯支付 异步回调成功 返回SUCCESS 正在异步回调商户 appId:{}", orderSerial.getAppId());
        CommonUtils.writeResponse(resp, "SUCCESS");

        // 回调并修改订单数据
        ruiXunPayService.callbackUser(callbackEntity);


    }

    @Override
    public void refreshConfig(HttpServletRequest req, HttpServletResponse resp)
    {
        certificateUtils.initMap();
    }
}
