package com.xqh.financial.controller.impl;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.controller.api.IVSPPayController;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.service.VSPPayService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.vsp.SybUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;

/**
 * 通联支付
 * Created by hssh on 2017/7/2.
 */
@RestController
public class VSPPayController implements IVSPPayController
{
    private static Logger logger = LoggerFactory.getLogger(VSPPayController.class);

    @Autowired
    private VSPPayService vspPayService;

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        logger.info("通联支付回调开始");
        TreeMap<String, String> params = SybUtil.getParams(req);
        logger.info("通联支付回调 参数params:{}", JSONObject.toJSON(params));

        // 校验参数
        PayOrderSerial orderSerial;

        try
        {
            orderSerial = vspPayService.verifyCallbackParam(params);
        }
        catch (ValidationException e)
        {
            logger.error("通联支付回调参数校验失败 msg:{}", e.getMessage());
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }


        // 插入订单
        CallbackEntity callbackEntity = vspPayService.insertOrderAndGenCallbackEntity(orderSerial, params);
        if(null == callbackEntity)
        {
            logger.error("通联支付回调 创建订单失败 orderSerial:{} params:{}", orderSerial, params);
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }


        // 通联支付回调成功
        logger.info("通联支付 回调返回success 正在异步回到商户 appId:{}", orderSerial.getAppId());
        CommonUtils.writeResponse(resp, "success");

        // 回调并修改订单数据
        vspPayService.callbackUser(callbackEntity);

    }
}
