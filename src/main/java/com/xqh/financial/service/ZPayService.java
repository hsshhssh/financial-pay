package com.xqh.financial.service;

import com.xqh.financial.entity.*;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.Constant;
import com.xqh.financial.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
    private PayOrderService payOrderService;


    /**
     * 掌易付支付接口
     */
    public void pay(HttpServletResponse resp, int userId, int appId, int money, int orderSerial, int payType, PayApp payApp) {

        PayPZI payPZI = payPZIService.select(userId, appId);
        if(null == payPZI) {
            logger.error("appId：{} 无配置掌易付支付通道 ", appId);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
        }


        String partnerId = payPZI.getZpayParentId();
        String currency = getCurrency(payType);
        String times = CommonUtils.getFormatDate("yyyyMMddHHmmss");
        String secretKey = payPZI.getZpayKey();
        String qn = payPZI.getZpayQn();
        String paymode = getPaymode(payType);
        String appName = payApp.getAppName();

        String sign = CommonUtils.getMd5(partnerId + appId + currency + money+times + secretKey);

        StringBuffer sb = new StringBuffer();
        sb.append("http://pay.csl2016.cn:8000/sp/theThirdPayWapEntrance.e?");
        sb.append("partnerId=" + partnerId +"&");
        sb.append("appId="+ appId +"&");
        sb.append("money="+ money +"&");
        sb.append("qn=" + qn + "&");
        sb.append("currency="+ currency +"&");
        sb.append("sign=" + sign + "&");
        sb.append("cpparam=" + orderSerial + "&");
        sb.append("notifyUrl=http://139.196.51.152:8080/xqh/financial/zpay/nodifyUrl/" + appId + "&");
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
    public CallbackEntity insertOrderAndGenCallbackEntity(HttpServletRequest req) {

        Integer cporderid = Integer.parseInt(req.getParameter("cporderid"));

        PayOrderSerial payOrderSerial = orderSerialMapper.selectByPrimaryKey(cporderid);

        // TODO 检验其他参数
        if(null == payOrderSerial) {
            logger.error("参数校验异常 cporderid:{} 无效", cporderid);
            return null;
        }

        PayApp payApp = payAppMapper.selectByPrimaryKey(payOrderSerial.getAppId());
        if(null == payApp) {
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

        String sign = CommonUtils.getMd5(callbackEntity.getOrderNo() + callbackEntity.getPayUserId() + callbackEntity.getAppId() + callbackEntity.getPayType() + payApp.getKey());
        callbackEntity.setSign(sign);


        // 插入订单数据
        PayOrder payOrder = new PayOrder();
        int nowTime = (int) (System.currentTimeMillis()/1000);
        payOrder.setOrderNo(callbackEntity.getOrderNo());
        payOrder.setOrderSerial(payOrderSerial.getId());
        payOrder.setUserId(payOrderSerial.getUserId());
        payOrder.setAppId(payOrderSerial.getAppId());
        payOrder.setMoney(payOrderSerial.getMoney());
        payOrder.setPlatformId(payOrderSerial.getPlatformId());
        payOrder.setPayType(payOrderSerial.getPayType());
        payOrder.setCallbackState(Constant.CALLBACK_FAIL); // 初始值为回调失败
        payOrder.setCreateTime(nowTime);
        payOrder.setUpdateTime(nowTime);

        // TODO 根据appId platformId取得利息
        PayAppPlatform appPlatform = appPlatformService.selectByAppIdPlatformId(payOrderSerial.getAppId(), payOrderSerial.getPlatformId());
        if(null == appPlatform) {
            logger.error("计算手续费比例失败 appPlatform = null  appId:{}, platformId:{}", payOrderSerial.getAppId(), payOrderSerial.getPlatformId());
            return null;
        }
        payOrder.setInterestRate(appPlatform.getInterestRate());


        payOrderMapper.insertSelective(payOrder);

        callbackEntity.setOrderId(payOrder.getId());
        callbackEntity.setCallbackUrl(payApp.getCallbackUrl());

        return callbackEntity;
    }

    @Async
    public void callbackUser(CallbackEntity callbackEntity)
    {
        logger.info("回调商户异步操作开始 orderId:{}", callbackEntity.getOrderId());

        String url = genCallbackUrl(callbackEntity);
        int orderId = callbackEntity.getOrderId();

        logger.info("回调商户 url {}", url);
        HttpResult httpResult = HttpUtils.get(url);

        logger.info("回调商户返回值: {}", httpResult);

        // TODO 增加重试机制
        int nowTime = (int) System.currentTimeMillis();
        if("1".equals(httpResult.getContent()))
        {
            // 成功
            logger.info("orderId:{} 回调商户成功", orderId);
            PayOrder payOrder = new PayOrder();
            payOrder.setId(orderId);
            payOrder.setCallbackState(Constant.CALLBACK_SUCCESS);
            payOrder.setCallbackSuccessTime(nowTime);
            payOrder.setUpdateTime(nowTime);
            int res = payOrderMapper.updateByPrimaryKeySelective(payOrder);
            if(res <= 0)
            {
                logger.error("支付商户成功 修改订单状态失败 orderId:{}", orderId);
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
                logger.error("支付商户成功 修改订单状态失败 orderId:{}", orderId);
            }

        }

        logger.info("回调商户异步操作结束 orderId:{}", callbackEntity.getOrderId());
    }


    /**
     * 生成商户回调地址
     * @param callbackEntity
     * @return
     */
    public String genCallbackUrl(CallbackEntity callbackEntity) {

        StringBuilder sb = new StringBuilder();
        sb.append(callbackEntity.getCallbackUrl() + "?");
        sb.append("orderNo=" + callbackEntity.getOrderNo() + "&");
        sb.append("payUserId=" + callbackEntity.getPayUserId() + "&");
        sb.append("appId=" + callbackEntity.getAppId() + "&");
        sb.append("payType=" + callbackEntity.getPayType() + "&");
        sb.append("userParam=" + callbackEntity.getUserParam() + "&");
        sb.append("userOrderNo=" + callbackEntity.getUserOrderNo() + "&");
        sb.append("sign=" + callbackEntity.getSign());

        return sb.toString();

    }





    private String getCurrency(int payType) {
        return "1000200010000000";
    }

    private String getPaymode(int payType) {
        return "1";
    }

}
