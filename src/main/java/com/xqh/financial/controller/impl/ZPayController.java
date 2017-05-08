package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IZPayController;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.entity.other.TempEntity;
import com.xqh.financial.service.XQHPayService;
import com.xqh.financial.service.ZPayService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.Constant;
import com.xqh.financial.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by hssh on 2017/5/7.
 */
@RestController
public class ZPayController implements IZPayController {

    private static Logger logger = LoggerFactory.getLogger(ZPayController.class);

    @Autowired
    private TempEntity tempEntity;

    @Autowired
    private ZPayService zPayService;

    @Autowired
    private XQHPayService xqhPayService;

    @Override
    public void nodifyUrl(@RequestParam(value = "result", required = false) int result,
                          @PathVariable("appId") int appId,
                          HttpServletRequest req,
                          HttpServletResponse resp) {
        logger.info("/nodifyUrl/appId: appId:{}, result:{}", appId, result);

        int res;
        if(1 == result) {
            // 成功
            logger.info("支付成功 appId:{}", appId);
            res = Constant.RESULT_SUCCESS;
        } else if(0 == result) {
            logger.info("用户取消支付 appId:{}", appId);
            res = Constant.RESULT_CANCEL_PAY;
        } else {
            logger.info("未知异常 appId:{}, result:{}", result);
            res = Constant.RESULT_UNKNOWN_ERROR;
        }
        xqhPayService.notifyResult(resp, tempEntity.getNotifyUrl(), res);

    }

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp) {

        CommonUtils.getRequestParam(req, "掌易付回调");

        CallbackEntity callbackEntity = zPayService.genCallbackEntity(req);

        logger.info("callbackEntity:{}", callbackEntity);

        StringBuilder sb = new StringBuilder();
        sb.append(tempEntity.getCallback() + "?");
        sb.append("orderNo=" + callbackEntity.getOrderNo() + "&");
        sb.append("payUserId=" + callbackEntity.getPayUserId() + "&");
        sb.append("appId=" + callbackEntity.getAppId() + "&");
        sb.append("payType=" + callbackEntity.getPayType() + "&");
        sb.append("userParam=" + callbackEntity.getUserParam() + "&");
        sb.append("userOrderNo=" + callbackEntity.getUserOrderNo() + "&");
        sb.append("sign=" + callbackEntity.getSign());

        logger.info("回调商户 url {}", sb.toString());
        HttpResult httpResult = HttpUtils.get(sb.toString());

        logger.info("回调商户返回值: {}", httpResult);

        try {
            resp.getWriter().print(httpResult.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
