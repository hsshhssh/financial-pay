package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.WFTPayDemoController;
import com.xqh.financial.controller.api.IXQHPayController;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayAppPlatform;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.service.*;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
    @Autowired
    private AppPlatformService appPlatformService;
    @Autowired
    private VSPPayService vspPayService;
    @Autowired
    private RuiXunPayService ruiXunPayService;
    @Resource
    private WFTPayService wftPayService;
    @Resource
    private XRYPayService xryPayService;

    @Override
    public void pay(HttpServletRequest req, HttpServletResponse resp) {

        CommonUtils.printRequestParam(req, "新企航支付请求" + req.getRequestURI());

        // 取得支付实体类
        PayEntity payEntity = null;

        try
        {
            payEntity = xqhPayService.genPayEntity(req);
        }
        catch (ValidationException ve)
        {
            logger.error("支付接口参数不符合要求 msg:{}", ve.getMessage());
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_PARAM);
            return ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("转换参数 未知异常 {}", e.getMessage());
            CommonUtils.writeResponse(resp, Constant.RESULT_UNKNOWN_ERROR);
            return ;
        }

        logger.info("payEntity:{}", payEntity);

        // 校验应用、用户信息
        PayApp payApp = payAppMapper.selectByPrimaryKey(payEntity.getAppId());
        if(null == payApp)
        {
            logger.error("无效appId:{}", payEntity.getAppId());
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_PARAM);
            return ;
        }
        if(payApp.getUserId() != payEntity.getUserId())
        {
            logger.error("无效用户Id:{}", payEntity.getUserId());
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_PARAM);
            return ;
        }

        //// 校验
        int verifyRes = xqhPayService.verifyParam(payEntity, payApp);
        if(verifyRes != 0)
        {
            logger.error("校验不通过 payEntity:{}", payEntity);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), verifyRes);
            return ;
        }

        //TODO 根据路由得到支付平台
        PayAppPlatform payAppPlatform = appPlatformService.selectValidRecordByAppIdPayType(payEntity.getAppId(), payEntity.getPayType());
        if(null == payAppPlatform)
        {
            logger.error("无支付通道或者支付通道异常 appId:{}, payType:{}", payEntity.getAppId(), payEntity.getPayType());
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
            return ;
        }
        payEntity.setPlatformId(payAppPlatform.getPlatformId());


        // 取得订到流水号
        xqhPayService.getOrderSerial(payEntity);


        logger.info("发起支付 payEntity:{}", payEntity);

        if(Constant.ZPAY_CHANNEL_CODE.equals(payAppPlatform.getPlatformCode()))
        {
            logger.info("掌易付支付通道 payEntity:{}", payEntity);
            zPayService.pay(resp, payEntity.getUserId(), payEntity.getAppId(), payEntity.getMoney(),payEntity.getOrderSerial(), payEntity.getPayType(), payApp, req);
        }
        else if(Constant.VSP_CHANNEL_CODE.equals(payAppPlatform.getPlatformCode()))
        {
            logger.info("通联支付通道 payEntity:{}", payEntity);
            vspPayService.pay(resp, payEntity.getUserId(), payEntity.getAppId(), payEntity.getMoney(),payEntity.getOrderSerial(), payEntity.getPayType(), payApp);
        }
        else if(Constant.RUIXUN_CHANNEL_CODE.equals(payAppPlatform.getPlatformCode()))
        {
            logger.info("锐讯支付通道 payEntity:{}", payEntity);
            String ip = CommonUtils.getIp(req);
            logger.info("锐讯支付 client ip:{}", ip);
            ruiXunPayService.pay(resp, payEntity.getUserId(), payEntity.getAppId(), payEntity.getMoney(),payEntity.getOrderSerial(), payEntity.getPayType(), payApp, ip, payAppPlatform.getInterestRate(), payEntity.getOpenId(), req);
        }
        else if(Constant.WFT_CHANNEL_CODE.equals(payAppPlatform.getPlatformCode()))
        {
            logger.info("威富通支付通道 payEntity:{}", payEntity);
            wftPayService.pay(req, resp, payEntity, payApp);
        }
        else if(Constant.XRY_CHANNEL_CODE.equals(payAppPlatform.getPlatformCode()))
        {
            logger.info("新瑞云支付通道 payEntity:{}", payEntity);
            xryPayService.pay(payEntity, payApp, req, resp);
        }
        else
        {
            logger.error("支付通道无效 payAppPlayform: {}", payAppPlatform);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
        }

    }
}
