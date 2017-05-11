package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IXQHPayController;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.mapper.PayAppMapper;
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
    private ZPayService zPayService;

    @Autowired
    private PayAppMapper payAppMapper;

    @Override
    public void pay(HttpServletRequest req, HttpServletResponse resp) {

        CommonUtils.getRequestParam(req, "新企航支付请求" + req.getRequestURI());

        // 取得支付实体类
        PayEntity payEntity = null;

        try {
            payEntity = xqhPayService.genPayEntity(req);
        } catch (ValidationException ve) {
            logger.error("支付接口参数不符合要求 msg:{}", ve.getMessage());
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_PARAM);
            return ;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("转换参数 未知异常 {}", e.getMessage());
            CommonUtils.writeResponse(resp, Constant.RESULT_UNKNOWN_ERROR);
            return ;
        }

        logger.info("payEntity:{}", payEntity);

        // 校验应用、用户信息
        PayApp payApp = payAppMapper.selectByPrimaryKey(payEntity.getAppId());
        if(null == payApp) {
            logger.error("无效appId:{}", payEntity.getAppId());
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_PARAM);
        }
        if(payApp.getUserId() != payEntity.getPayUserId()) {
            logger.error("无效用户Id:{}", payEntity.getPayUserId());
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_PARAM);
        }

        // 校验
        int verifyRes = xqhPayService.verifyParam(payEntity, payApp);
        if(verifyRes != 0) {
            logger.error("校验不通过 payEntity:{}", payEntity);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), verifyRes);
            return ;
        }

        //TODO 根据路由得到支付平台
        int platfomId = 1;
        payEntity.setPlatformId(platfomId);

        // 取得订到流水号
        xqhPayService.getOrderSerial(payEntity);


        logger.info("发起支付 payEntity:{}", payEntity);

        zPayService.pay(resp, payEntity.getPayUserId(), payEntity.getAppId(), payEntity.getMoney(), payEntity.getPayType(),payEntity.getOrderSerial(), payApp);
    }
}
