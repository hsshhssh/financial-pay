package com.xqh.financial.service;

import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.exception.RepeatPayException;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String sign = CommonUtils.getMd5(payEntity.getUserId() + payEntity.getAppId() + payEntity.getMoney() + payEntity.getTime() + payApp.getSecretkey());

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


}
