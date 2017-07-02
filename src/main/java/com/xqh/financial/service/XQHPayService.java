package com.xqh.financial.service;

import com.xqh.financial.entity.*;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.exception.RepeatPayException;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.mapper.PayCFRMapper;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;

/**
 * Created by hssh on 2017/5/7.
 */
@Service
public class XQHPayService {

    private static Logger logger = LoggerFactory.getLogger(XQHPayService.class);

    @Autowired
    private OrderSerialService orderSerialService;

    @Autowired
    private PayAppMapper payAppMapper;

    @Autowired
    private ConfigParamsUtils paramsUtils;

    @Autowired
    private AppPlatformService appPlatformService;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private PayCFRMapper payCFRMapper;

    /**
     * 从请求参数中获得约定的支付参数
     * @param req
     * @return
     */
    public PayEntity genPayEntity(HttpServletRequest req) {

        String payUserIdStr = req.getParameter("payUserId");
        String appIdStr = req.getParameter("appId");
        String moneyStr = req.getParameter("money");
        String timeStr = req.getParameter("time");
        String payTypeStr = req.getParameter("payType");
        String sign = req.getParameter("sign");
        String userOrderNo = req.getParameter("userOrderNo");
        String userParam = req.getParameter("userParam");

        PayEntity payEntity = new PayEntity();
        payEntity.setUserId(Integer.valueOf(payUserIdStr));
        payEntity.setAppId(Integer.valueOf(appIdStr));
        payEntity.setMoney(Integer.valueOf(moneyStr));
        payEntity.setTime(Integer.valueOf(timeStr));
        payEntity.setPayType(Integer.valueOf(payTypeStr));
        payEntity.setSign(sign);
        payEntity.setUserOrderNo(userOrderNo);
        payEntity.setUserParam(userParam);

        ValidateUtils.validateEntity(payEntity);

        return payEntity;

    }

    /**
     * 校验时间
     * 校验签名
     * @param payEntity
     */
    public int verifyParam(PayEntity payEntity, PayApp payApp) {

        if("1".equals(paramsUtils.getDebugFlag()))
        {
            logger.info("跳过支付参数校验");
            return 0;
        }

        // 校验时间
        int nowTime = (int) (currentTimeMillis()/1000);
        if(nowTime - payEntity.getTime() > 300) {
            logger.info("支付超时 time:{}", payEntity.getTime());
            return Constant.RESULT_TIME_OUT;
        }


        // 校验sign
        String sign = CommonUtils.getMd5("" + payEntity.getUserId() + payEntity.getAppId() + payEntity.getMoney() + payEntity.getTime() + payApp.getSecretkey());

        if(!sign.equals(payEntity.getSign())) {
            logger.error("新企航支付参数校验失败 payEntity:{}", payEntity);
            return Constant.RESULT_INVALID_SIGN;
        }

        return 0;
    }

    /**
     * 取得订单流水号
     * @param payEntity
     */
    public void getOrderSerial(PayEntity payEntity) throws RepeatPayException
    {

        PayOrderSerial payOrderSerial = new PayOrderSerial();
        payOrderSerial.setUserId(payEntity.getUserId());
        payOrderSerial.setAppId(payEntity.getAppId());
        payOrderSerial.setPayType(payEntity.getPayType());
        payOrderSerial.setMoney(payEntity.getMoney());
        payOrderSerial.setPlatformId(payEntity.getPlatformId());
        payOrderSerial.setUserOrderNo(payEntity.getUserOrderNo());
        payOrderSerial.setUserParam(payEntity.getUserParam());
        payOrderSerial.setRequestTime(payEntity.getTime()/300);

        int orderSerial = orderSerialService.insert(payOrderSerial);
        logger.info("订单流水号 orderSerial:{}", orderSerial);
        payEntity.setOrderSerial(orderSerial);
    }


    /**
     * 返回通知
     * @param notifyUrl
     * @param result
     */
    public void notifyResult(HttpServletResponse resp, String notifyUrl, int result) {

        String url = UrlUtils.UrlPage(notifyUrl);
        if(url != null) {
            try {
                resp.sendRedirect(url + "?result=" + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 取得订单号
     * @param orderSerial
     * @return
     */
    public String genOrderNO(int orderSerial) {
        String orderSerialStr = String.format("%010d", orderSerial);
        System.currentTimeMillis();
        return "020" + System.currentTimeMillis() + orderSerialStr;
    }

    /**
     * 通过支付流水实体生成回调实体
     */
    public CallbackEntity getCallbackByOrderSerial(PayOrderSerial orderSerial, String key)
    {
        CallbackEntity callbackEntity = new CallbackEntity();
        callbackEntity.setOrderNo(genOrderNO(orderSerial.getId()));
        callbackEntity.setPayUserId(orderSerial.getUserId());
        callbackEntity.setAppId(orderSerial.getAppId());
        callbackEntity.setPayType(orderSerial.getPayType());
        callbackEntity.setUserParam(orderSerial.getUserParam());// 目前只传1
        callbackEntity.setUserOrderNo(orderSerial.getUserOrderNo());

        String sign = CommonUtils.getMd5(callbackEntity.getOrderNo() + callbackEntity.getPayUserId() + callbackEntity.getAppId() + callbackEntity.getPayType() + key);
        callbackEntity.setSign(sign);

        return callbackEntity;
    }

    /**
     * 通过支付流水实体创建订单
     */
    public PayOrder insertOrderByOrderSerial(PayOrderSerial orderSerial, String orderNo, String platformOrderNo)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(orderNo);
        payOrder.setOrderSerial(orderSerial.getId());
        payOrder.setUserOrderNo(orderSerial.getUserOrderNo());
        payOrder.setUserId(orderSerial.getUserId());
        payOrder.setAppId(orderSerial.getAppId());
        payOrder.setMoney(orderSerial.getMoney());
        payOrder.setPlatformId(orderSerial.getPlatformId());
        payOrder.setPayType(orderSerial.getPayType());
        payOrder.setCallbackState(Constant.CALLBACK_FAIL); // 初始值为回调失败
        payOrder.setPlatformOrderNo(platformOrderNo);
        payOrder.setCreateTime(nowTime);
        payOrder.setUpdateTime(nowTime);

        // 取得利息
        PayAppPlatform appPlatform = appPlatformService.selectByAppIdPlatformId(orderSerial.getAppId(), orderSerial.getPlatformId());
        if(null == appPlatform)
        {
            logger.error("计算手续费比例失败 appPlatform = null  appId:{}, platformId:{}", orderSerial.getAppId(), orderSerial.getPlatformId());
            return null;
        }
        payOrder.setInterestRate(appPlatform.getInterestRate());
        payOrderMapper.insertSelective(payOrder);

        return payOrder;
    }

    /**
     * 生成失败的回调记录
     */
    public PayCFR insertCFR(PayOrderSerial orderSerial, CallbackEntity callbackEntity, PayOrder payOrder)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        PayCFR payCFR = new PayCFR();
        payCFR.setUserId(orderSerial.getUserId());
        payCFR.setAppId(orderSerial.getAppId());
        payCFR.setOrderNo(payOrder.getOrderNo());
        payCFR.setUserOrderNo(payOrder.getUserOrderNo());
        payCFR.setOrderId(payOrder.getId());
        payCFR.setMoney(orderSerial.getMoney());
        payCFR.setCallbackUrl(genCallbackUrl(callbackEntity));
        payCFR.setState(Constant.FAIL_STATE);
        payCFR.setCreateTime(nowTime);
        payCFR.setUpdateTime(nowTime);

        payCFRMapper.insertSelective(payCFR);

        return payCFR;

    }

    /**
     * 生成商户回调地址
     * @param callbackEntity
     * @return
     */
    private String genCallbackUrl(CallbackEntity callbackEntity) {

        StringBuilder sb = new StringBuilder();
        sb.append(callbackEntity.getCallbackUrl() + "?");
        sb.append("orderNo=" + callbackEntity.getOrderNo() + "&");
        sb.append("userId=" + callbackEntity.getPayUserId() + "&");
        sb.append("appId=" + callbackEntity.getAppId() + "&");
        sb.append("payType=" + callbackEntity.getPayType() + "&");
        sb.append("userParam=" + callbackEntity.getUserParam() + "&");
        sb.append("userOrderNo=" + callbackEntity.getUserOrderNo() + "&");
        sb.append("sign=" + callbackEntity.getSign());

        return sb.toString();

    }



    /**
     * 根据回到接口修改订单状态
     */
    @Transactional
    public void updateOrderStatus(HttpResult httpResult, int orderId, int crfId)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);
        if(Constant.CALLBACK_SUCCESS_RESULT.equals(httpResult.getContent()))
        {
            // 成功
            logger.info("orderId:{} 回调商户成功", orderId);

            // 修改订单状态
            PayOrder payOrder = new PayOrder();
            payOrder.setId(orderId);
            payOrder.setCallbackState(Constant.CALLBACK_SUCCESS);
            payOrder.setCallbackSuccessTime(nowTime);
            payOrder.setUpdateTime(nowTime);
            int resOrder = payOrderMapper.updateByPrimaryKeySelective(payOrder);
            if(resOrder <= 0)
            {
                logger.error("回调商户成功 修改订单状态失败 orderId:{}", orderId);
            }

            // 修改回调记录状态
            PayCFR payCFR = new PayCFR();
            payCFR.setId(crfId);
            payCFR.setState(Constant.SUCCESS_STATE);
            payCFR.setSuccessTime(nowTime);
            payCFR.setLastCallTime(nowTime);
            payCFR.setUpdateTime(nowTime);
            int resCRF = payCFRMapper.updateByPrimaryKeySelective(payCFR);
            if(resCRF <= 0)
            {
                logger.error("回调商户商户成功 修改回调状态失败 payCFRId:{}", crfId);
            }

        }
        else
        {
            // 失败
            logger.error("orderId:{} 回调商户失败", orderId);
            PayOrder payOrder = new PayOrder();
            payOrder.setId(orderId);
            payOrder.setCallbackState(Constant.CALLBACK_FAIL);
            payOrder.setCallbackFailTime(nowTime);
            payOrder.setUpdateTime(nowTime);
            int res = payOrderMapper.updateByPrimaryKeySelective(payOrder);
            if(res <= 0)
            {
                logger.error("回调商户失败 修改订单状态失败 orderId:{}", orderId);
            }

            // 修改回调记录状态
            PayCFR payCFR = new PayCFR();
            payCFR.setId(crfId);
            payCFR.setState(Constant.FAIL_STATE);
            payCFR.setLastCallTime(nowTime);
            payCFR.setUpdateTime(nowTime);
            int resCRF = payCFRMapper.updateByPrimaryKeySelective(payCFR);
            if(resCRF <= 0)
            {
                logger.error("回调商户失败 修改回调状态失败 payCFRId:{}", crfId);
            }


        }
    }

}
