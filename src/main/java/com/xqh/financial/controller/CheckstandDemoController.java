package com.xqh.financial.controller;

import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by hssh on 2017/7/17.
 */
@RequestMapping("/checkstand")
@RestController
public class CheckstandDemoController
{

    private static final Logger logger = LoggerFactory.getLogger(CheckstandDemoController.class);

    @Autowired
    private ConfigParamsUtils config;

    @GetMapping("index")
    public void index(HttpServletResponse resp, HttpServletRequest req) throws IOException
    {
        int payUserId = 2;
        int appId = 8;
        int money = 1;
        String key = "A85CE8F77D2917013D4963CEC6B7522E";
        String sign = CommonUtils.getMd5("" + payUserId + appId + money + key);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("/checkstand.html");
        urlBuilder.append("?payUserId=" + payUserId);
        urlBuilder.append("&appId=" + appId);
        urlBuilder.append("&money=" + money);
        urlBuilder.append("&sign=" + sign);

        logger.info("新企航收银台请求地址url:{}", urlBuilder.toString());

        resp.sendRedirect(urlBuilder.toString());
    }


    @GetMapping("pay")
    public void pay(HttpServletResponse resp, HttpServletRequest req) throws IOException {
        int payUserId = Integer.parseInt(req.getParameter("payUserId"));
        int appId = Integer.parseInt(req.getParameter("appId"));
        int money = Integer.parseInt(req.getParameter("money"));
        int payType = Integer.parseInt(req.getParameter("payType"));
        String sign = req.getParameter("sign");

        // TODO 取得key
        String key = "A85CE8F77D2917013D4963CEC6B7522E";

        // 校验参数
        String _sign = CommonUtils.getMd5("" + payUserId + appId + money + key);
        if(!sign.equals(_sign))
        {
            logger.error("新企航收银台支付 校验不通过 appId:{}", appId);
            CommonUtils.writeResponse(resp, Constant.RESULT_INVALID_SIGN);
            return;
        }

        // 支付
        int time = (int) (System.currentTimeMillis()/1000);
        String _signPay = CommonUtils.getMd5("" + payUserId + appId + money + time + key);
        StringBuffer payUrlBuffer = new StringBuffer();
        payUrlBuffer.append(config.getZpayNotifyHost() + "/xqh/financial/pay");
        payUrlBuffer.append("?payUserId=" + payUserId);
        payUrlBuffer.append("&appId=" + appId);
        payUrlBuffer.append("&money=" + money);
        payUrlBuffer.append("&time=" + time);
        payUrlBuffer.append("&sign=" + _signPay);
        payUrlBuffer.append("&payType=" + payType);
        payUrlBuffer.append("&userOrderNo=" + "userOrderNo");
        payUrlBuffer.append("&userParam=" + "userParam");

        logger.info("新企航支付收银台支付url:{}", payUrlBuffer.toString());

        resp.sendRedirect(payUrlBuffer.toString());
    }

}
