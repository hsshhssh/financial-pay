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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
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
                          @PathParam("appId") int appId,
                          HttpServletRequest req,
                          HttpServletResponse resp) {
        logger.info("/nodifyUrl/appId: appId:{}, result:{}", appId, result);

        if(1 == result) {
            xqhPayService.notifyResult(resp, tempEntity.getNotifyUrl(), Constant.RESULT_SUCCESS);
        } else {
            xqhPayService.notifyResult(resp, tempEntity.getNotifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
        }

    }

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp) {

        CommonUtils.getRequestParam(req, req.getRequestURI());

        CallbackEntity callbackEntity = zPayService.genCallbackEntity(req);

        StringBuilder sb = new StringBuilder();
        sb.append(tempEntity.getCallback() + "?");
        sb.append("orderNo=" + callbackEntity.getOrderNo() + "&");
        sb.append("payUserId=" + callbackEntity.getPayUserId() + "&");
        sb.append("appId=" + callbackEntity.getAppId() + "&");
        sb.append("payType=" + callbackEntity.getPayType() + "&");
        sb.append("userParam=" + callbackEntity.getUserParam() + "&");
        sb.append("userOrderNo=" + callbackEntity.getUserOrderNo() + "&");
        sb.append("sign=" + callbackEntity.getSign() + "&");

        logger.info("callback url {}", sb.toString());
        HttpResult httpResult = HttpUtils.get(sb.toString());

        try {
            resp.getWriter().print(httpResult.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
