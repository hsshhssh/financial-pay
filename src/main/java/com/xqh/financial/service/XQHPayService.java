package com.xqh.financial.service;

import com.xqh.financial.entity.other.PayEntity;
import com.xqh.financial.utils.UrlUtils;
import com.xqh.financial.utils.ValidateUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by hssh on 2017/5/7.
 */
@Service
public class XQHPayService {


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
        payEntity.setPayUserId(payUserIdStr);
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



}
