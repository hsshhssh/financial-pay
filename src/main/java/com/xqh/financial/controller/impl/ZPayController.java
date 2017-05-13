package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IZPayController;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.service.XQHPayService;
import com.xqh.financial.service.ZPayService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/5/7.
 */
@RestController
public class ZPayController implements IZPayController {

    private static Logger logger = LoggerFactory.getLogger(ZPayController.class);

    @Autowired
    private ZPayService zPayService;

    @Autowired
    private XQHPayService xqhPayService;

    @Autowired
    private PayAppMapper payAppMapper;

    @Override
    public void nodifyUrl(@RequestParam(value = "result", required = false) int result,
                          @PathVariable("appId") int appId,
                          HttpServletRequest req,
                          HttpServletResponse resp) {
        logger.info("/nodifyUrl/appId: appId:{}, result:{}", appId, result);

        PayApp payApp = payAppMapper.selectByPrimaryKey(appId);

        if(payApp == null) {
            logger.error("appId:{} 无效", appId);
            return ;
        }

        int res;
        if(1 == result)
        {
            // 成功
            logger.info("支付成功 appId:{}", appId);
            res = Constant.RESULT_SUCCESS;
        } else if(2 == result) {
            logger.info("用户取消支付 appId:{}", appId);
            res = Constant.RESULT_CANCEL_PAY;
        } else {
            logger.info("未知异常 appId:{}, result:{}",appId, result);
            res = Constant.RESULT_UNKNOWN_ERROR;
        }
        xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), res);

    }

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp) {

        CommonUtils.getRequestParam(req, "掌易付回调");

        CallbackEntity callbackEntity = null;
        try {
            callbackEntity = zPayService.insertOrderAndGenCallbackEntity(req);
        } catch (DuplicateKeyException e) {
            logger.warn("掌易付 重复回调 订单流水号{}", req.getParameter("cporderid"));
            return ;
        }

        if(null == callbackEntity) {
            logger.error("掌易付回调异常 callbackEntity=null");
            CommonUtils.writeResponse(resp, 1);
            return ;
        }

        // 掌易付回调成功
        CommonUtils.writeResponse(resp, 0);

        logger.info("callbackEntity:{}", callbackEntity);

        zPayService.callbackUser(callbackEntity);

    }
}
