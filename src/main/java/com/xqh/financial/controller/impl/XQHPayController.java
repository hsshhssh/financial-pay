package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IXQHPayController;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.entity.other.TempEntity;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.service.XQHPayService;
import com.xqh.financial.service.ZPayService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/5/7.
 */
@RestController
public class XQHPayController implements IXQHPayController{

    private static Logger logger = LoggerFactory.getLogger(IXQHPayController.class);

    @Autowired
    private XQHPayService xqhPayService;

    @Autowired
    private TempEntity tempEntity;

    @Autowired
    private ZPayService zPayService;

    @Override
    public void pay(HttpServletRequest req, HttpServletResponse resp) {

        CommonUtils.getRequestParam(req, "新企航支付请求");

        PayEntity payEntity = null;

        try {
            payEntity = xqhPayService.genPayEntity(req);
        } catch (ValidationException ve) {
            logger.error("支付接口参数不符合要求 msg:{}", ve.getMessage());
            xqhPayService.notifyResult(resp, tempEntity.getNotifyUrl(), Constant.RESULT_INVALID_PARAM);
            return ;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("转换参数 未知异常 {}", e.getMessage());
            xqhPayService.notifyResult(resp, tempEntity.getNotifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
            return ;
        }

        logger.info("payEntity:{}", payEntity);

        // 校验sign
        String sign = CommonUtils.getMd5(payEntity.getPayUserId() + payEntity.getAppId() + payEntity.getMoney() + payEntity.getTime() + tempEntity.getSecretKey());
        if(!sign.equals(payEntity.getSign())) {
            logger.error("新企航支付参数校验失败 payEntity:{}", payEntity);
            xqhPayService.notifyResult(resp, tempEntity.getNotifyUrl(), Constant.RESULT_INVALID_SIGN);
            return;
        }

        logger.info("发起支付");
        // 根据路由得到支付平台
        zPayService.pay(resp, payEntity.getPayUserId(), payEntity.getAppId(), payEntity.getMoney(), payEntity.getUserOrderNo());
    }
}
