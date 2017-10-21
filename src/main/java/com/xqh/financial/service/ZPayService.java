package com.xqh.financial.service;

import com.xqh.financial.entity.*;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.mapper.PayCFRMapper;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.Constant;
import com.xqh.financial.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by hssh on 2017/5/7.
 */
@Service
public class ZPayService {

    private static Logger logger = LoggerFactory.getLogger(ZPayService.class);

    @Autowired
    private PayPZIService payPZIService;

    @Autowired
    private XQHPayService xqhPayService;

    @Autowired
    private PayOrderSerialMapper orderSerialMapper;

    @Autowired
    private PayAppMapper payAppMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private AppPlatformService appPlatformService;

    @Autowired
    private PayCFRMapper payCFRMapper;

    @Autowired
    private ConfigParamsUtils configParamsUtils;


    /**
     * 掌易付支付接口
     */
    public void pay(HttpServletResponse resp, int userId, int appId, int money, int orderSerial, int payType, PayApp payApp, HttpServletRequest req) {

        PayPZI payPZI = payPZIService.select(userId, appId);
        if(null == payPZI) {
            logger.error("appId：{} 无配置掌易付支付通道 ", appId);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
        }


        String partnerId = payPZI.getZpayParentId();
        int zpayAppId = payPZI.getZpayAppId();
        String currency = getCurrency(payType);
        String times = CommonUtils.getFormatDate("yyyyMMddHHmmss");
        String secretKey = payPZI.getZpayKey();
        String qn = payPZI.getZpayQn();
        String paymode = getPaymode(payType);
        String appName = payApp.getAppName() + xqhPayService.getAppNameSuffix(orderSerial);

        String sign = CommonUtils.getMd5(partnerId + zpayAppId + currency + money+times + secretKey);

        StringBuffer sb = new StringBuffer();
        sb.append("http://pay.csl2016.cn:8000/sp/theThirdPayWapEntrance.e?");
        sb.append("partnerId=" + partnerId +"&");
        sb.append("appId="+ zpayAppId +"&");
        sb.append("money="+ money +"&");
        sb.append("qn=" + qn + "&");
        sb.append("currency="+ currency +"&");
        sb.append("sign=" + sign + "&");
        sb.append("cpparam=" + orderSerial + "&");

        String redirectUrl = null;
        String paramRedirectUrl = req.getParameter("redirectUrl");
        if(StringUtils.isNotBlank(paramRedirectUrl))
        {
            logger.info("参数决定跳转地址 redirectUrl:{}", paramRedirectUrl);
            try
            {
                redirectUrl = URLEncoder.encode(paramRedirectUrl, "utf8");
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            logger.info("后台配置决定跳转地址");
            redirectUrl = configParamsUtils.getZpayNotifyHost() + "/xqh/financial/zpay/nodifyUrl/" + appId;
        }

        sb.append("notifyUrl=" + redirectUrl + "&");
        String name = null;
        try {
            name = java.net.URLEncoder.encode(appName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("appFeeName=" + name + "&");
        sb.append("paymode=" + paymode + "&");
        sb.append("times=" + times);
        logger.info("请求掌易付支付接口：{}", sb.toString());

        try {
            resp.sendRedirect(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 掌易付回调处理：
     *  生成订单
     *  返回商户回调实体类
     * @param req
     * @return
     */
    @Transactional
    public CallbackEntity insertOrderAndGenCallbackEntity(HttpServletRequest req)
    {

        Integer cporderid = Integer.parseInt(req.getParameter("cporderid"));
        String platformOrderNo = req.getParameter("orderid");

        PayOrderSerial payOrderSerial = orderSerialMapper.selectByPrimaryKey(cporderid);

        // TODO 检验其他参数
        if(null == payOrderSerial)
        {
            logger.error("参数校验异常 cporderid:{} 无效", cporderid);
            return null;
        }

        PayApp payApp = payAppMapper.selectByPrimaryKey(payOrderSerial.getAppId());
        if(null == payApp)
        {
            logger.error("参数校验失败 appId:{} 无效", payOrderSerial.getAppId());
            return null;
        }

        // 生成回调实体类
        CallbackEntity callbackEntity = new CallbackEntity();
        callbackEntity.setOrderNo(xqhPayService.genOrderNO(payOrderSerial.getId()));
        callbackEntity.setPayUserId(payOrderSerial.getUserId());
        callbackEntity.setAppId(payOrderSerial.getAppId());
        callbackEntity.setPayType(payOrderSerial.getPayType()); // 暂时只有微信wap支付
        callbackEntity.setUserParam(payOrderSerial.getUserParam());// 目前只传1
        callbackEntity.setUserOrderNo(payOrderSerial.getUserOrderNo());

        String sign = CommonUtils.getMd5(callbackEntity.getOrderNo() + callbackEntity.getPayUserId() + callbackEntity.getAppId() + callbackEntity.getPayType() + payApp.getSecretkey());
        callbackEntity.setSign(sign);


        // 插入订单数据
        PayOrder payOrder = new PayOrder();
        int nowTime = (int) (System.currentTimeMillis()/1000);
        payOrder.setOrderNo(callbackEntity.getOrderNo());
        payOrder.setOrderSerial(payOrderSerial.getId());
        payOrder.setOrderSerialSuffix(Integer.valueOf(xqhPayService.getAppNameSuffix(payOrderSerial.getId())));
        payOrder.setUserOrderNo(payOrderSerial.getUserOrderNo());
        payOrder.setUserId(payOrderSerial.getUserId());
        payOrder.setAppId(payOrderSerial.getAppId());
        payOrder.setMoney(payOrderSerial.getMoney());
        payOrder.setPlatformId(payOrderSerial.getPlatformId());
        payOrder.setPayType(payOrderSerial.getPayType());
        payOrder.setCallbackState(Constant.CALLBACK_FAIL); // 初始值为回调失败
        payOrder.setPlatformOrderNo(platformOrderNo);
        payOrder.setCreateTime(nowTime);
        payOrder.setUpdateTime(nowTime);

        // TODO 根据appId platformId取得利息
        PayAppPlatform appPlatform = appPlatformService.selectByAppIdPlatformId(payOrderSerial.getAppId(), payOrderSerial.getPlatformId());
        if(null == appPlatform)
        {
            logger.error("计算手续费比例失败 appPlatform = null  appId:{}, platformId:{}", payOrderSerial.getAppId(), payOrderSerial.getPlatformId());
            return null;
        }
        payOrder.setInterestRate(appPlatform.getInterestRate());

        payOrderMapper.insertSelective(payOrder);


        callbackEntity.setOrderId(payOrder.getId());
        callbackEntity.setCallbackUrl(payApp.getCallbackUrl());

        // 插入回调失败记录表
        PayCFR payCFR = new PayCFR();
        payCFR.setUserId(payOrderSerial.getUserId());
        payCFR.setAppId(payOrderSerial.getAppId());
        payCFR.setOrderNo(payOrder.getOrderNo());
        payCFR.setUserOrderNo(payOrder.getUserOrderNo());
        payCFR.setOrderId(payOrder.getId());
        payCFR.setMoney(payOrderSerial.getMoney());
        payCFR.setCallbackUrl(genCallbackUrl(callbackEntity));
        payCFR.setState(Constant.FAIL_STATE);
        payCFR.setCreateTime(nowTime);
        payCFR.setUpdateTime(nowTime);

        payCFRMapper.insertSelective(payCFR);

        callbackEntity.setCfrId(payCFR.getId());

        return callbackEntity;
    }

    @Async
    public void callbackUser(CallbackEntity callbackEntity)
    {
        logger.info("回调商户异步操作开始 orderId:{}", callbackEntity.getOrderId());

        String url = genCallbackUrl(callbackEntity);

        logger.info("回调商户 url {}", url);
        HttpResult httpResult = HttpUtils.get(url);

        logger.info("回调商户返回值: {}", httpResult);

        // TODO 增加重试机制
        updateOrderStatus(httpResult, callbackEntity.getOrderId(), callbackEntity.getCfrId());

        logger.info("回调商户异步操作结束 orderId:{}", callbackEntity.getOrderId());
    }

    /**
     * 使用XQHPayService updateOrderStatus
     */
    @Transactional
    @Deprecated
    private void updateOrderStatus(HttpResult httpResult, int orderId, int crfId)
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


    /**
     * 生成商户回调地址
     * 使用XQHPayService的genCallbackUrl方法
     */
    @Deprecated
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
     * 通过本平台支付方式获得掌易付支付方式
     * 默认为微信
     */
    private String getCurrency(int payType) {
        if(Constant.ALIPAYWAP_PAY_TYPE == payType)
        {
            return "1000200020000000";
        }
        else
        {
            return "1000200010000000";
        }
    }

    /**
     * 通过本平台支付方式获得掌易付支付方式
     * 默认问微信
     */
    private String getPaymode(int payType) {
        if(Constant.ALIPAYWAP_PAY_TYPE == payType)
        {
            return "2";
        }
        else
        {
            return "1";
        }

    }

}
