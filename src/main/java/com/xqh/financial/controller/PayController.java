package com.xqh.financial.controller;


import com.xqh.financial.utils.CommonUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by hssh on 2017/4/30.
 */
@RestController
public class PayController {

    private static Logger logger = LoggerFactory.getLogger(PayController.class);

    @RequestMapping("/pay")
    public void pay(@RequestParam("money") int money) {
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
        sb.append("cpparam=1&");
        sb.append("notifyUrl=http://139.196.51.152:8080/nodifyUrl&");
        String name = null;
        try {
            name = java.net.URLEncoder.encode("测试项目","utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("appFeeName=" + name + "&");
        sb.append("paymode=1&");
        sb.append("times=" + times);
        System.out.println(sb.toString());

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(sb.toString());

    }


    @RequestMapping("/nodifyUrl")
    public int nodifyUrl(@RequestParam("result") int result) {
        logger.info("nodifyUri result:{}", result);
        return result;
    }

    @RequestMapping("/pay/callback")
    public String callback(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        return queryString.substring(0, queryString.length() - 1);
    }
}
