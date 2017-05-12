package com.xqh.financial.controller;


import com.xqh.financial.service.ZPayService;
import com.xqh.financial.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by hssh on 2017/4/30.
 */
@RestController
public class PayController {

    private static Logger logger = LoggerFactory.getLogger(PayController.class);

    @Autowired
    ZPayService zPayService;

    @RequestMapping("/pay")
    public void pay(@RequestParam("money") int money, HttpServletResponse resp) {
        String partnerId = "1000100020001163";
        String appId = "3061";
        String currency = "1000200010000000";
        String times = CommonUtils.getFormatDate("yyyyMMddHHmmss");
        String secretKey = "A3F4A7E77AD7474E9105AD5B7DFB8240";

        String sign = CommonUtils.getMd5(partnerId + appId + currency + money+times + secretKey);

        StringBuffer sb = new StringBuffer();
        sb.append("http://pay.csl2016.cn:8000/sp/theThirdPayWapEntrance.e?");
        sb.append("partnerId=" + partnerId +"&");
        sb.append("appId="+ appId +"&");
        sb.append("money="+ money +"&");
        sb.append("qn=zyap3061_56450_100&");
        sb.append("currency="+ currency +"&");
        sb.append("sign=" + sign + "&");
        sb.append("cpparam=abc&");
        sb.append("notifyUrl=http://139.196.51.152:8080/nodifyUrl?num=aaa&");
        String name = null;
        try {
            name = java.net.URLEncoder.encode("测试项目","utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("appFeeName=" + name + "&");
        sb.append("paymode=1&");
        sb.append("times=" + times);
        logger.info(sb.toString());

        try {
            resp.sendRedirect(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @RequestMapping("/nodifyUrl")
    public int nodifyUrl(@RequestParam(value="result", required = false) int result,
                         HttpServletRequest request) {
        logger.info("nodifyUri result:{}", result);
        logger.info(request.getRequestURI());
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        if(StringUtils.isNotBlank(queryString)) {
            logger.info("/nodifyUrl Param: " + queryString.substring(0, queryString.length() - 1));
        } else {
            logger.info("/nodifyUrl Param: no param");
        }

        return result;
    }

    @RequestMapping("/pay/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        logger.info("/pay/callbackUrl param: " + queryString.substring(0, queryString.length() - 1));
        try {
            //Thread.sleep(20000);
            response.getWriter().print(1);
        } catch (IOException e) {
            throw new RuntimeException();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        }
    }





}
