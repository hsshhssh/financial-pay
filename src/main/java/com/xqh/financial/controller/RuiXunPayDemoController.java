package com.xqh.financial.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.HttpsUtils;
import com.xqh.financial.utils.ruixun.SignUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/7/8.
 */
@RestController
@RequestMapping("/ruixun")
public class RuiXunPayDemoController
{
    private static Logger logger = LoggerFactory.getLogger(RuiXunPayDemoController.class);

    @GetMapping("pay")
    public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp, @RequestParam(value = "openid", required = false) String openid) throws Exception {
        String ip = CommonUtils.getIp(req);

        openid = openid == null ? "c40CB97KHlOubgXyb7AkIesBuo1mRNEG" : openid;

        logger.info("openid :{}", openid);

        String url = "https://mpay.wxhang.cn/gateway";
        List<BasicNameValuePair> nvps = Lists.newArrayList();
        nvps.add(new BasicNameValuePair("appid", "mp_acde240135eb3054"));
        nvps.add(new BasicNameValuePair("requestNo", CommonUtils.getFormatDate("yyyyMMddHHmmssSSS")));
        nvps.add(new BasicNameValuePair("productId", "0105"));
        nvps.add(new BasicNameValuePair("transId", "10"));
        nvps.add(new BasicNameValuePair("orderDate", String.valueOf(System.currentTimeMillis()/1000))); // 应用场境需要转成 精确到 秒的时间戳
        nvps.add(new BasicNameValuePair("orderNo", CommonUtils.getFormatDate("yyyyMMddHHmmss")));
        nvps.add(new BasicNameValuePair("returnUrl", "http://139.196.51.152:8080/ruixun/notify"));
        nvps.add(new BasicNameValuePair("notifyUrl", "http://139.196.51.152:8080/ruixun/callback"));
        nvps.add(new BasicNameValuePair("transAmt", String.valueOf(money)));
        nvps.add(new BasicNameValuePair("commodityName", "测试应用"));
        nvps.add(new BasicNameValuePair("merchantId", "301"));
        nvps.add(new BasicNameValuePair("ip", ip));
        nvps.add(new BasicNameValuePair("storeId", "444"));
        nvps.add(new BasicNameValuePair("openid", openid));
        nvps.add(new BasicNameValuePair("signature", SignUtils.signData(nvps, "1.0")));


        HttpResult httpResult = HttpsUtils.post(url, null, new UrlEncodedFormEntity(nvps, "UTF-8"), "UTF-8");
        if(httpResult.getStatus() == 200)
        {
            boolean signFlag = SignUtils.verferSignData(httpResult.getContent(), "1.0");
            if (!signFlag) {
                logger.error("验签失败");
            }
            logger.info("验签成功 result:{}", httpResult.getContent());
            List<String> contentList = Splitter.on("&").omitEmptyStrings().splitToList(httpResult.getContent());

            Map<String, String> contentMap;
            try
            {
                contentMap = convertResToMap(httpResult.getContent());
            }
            catch (ValidationException e)
            {
                logger.error("锐讯支付获得url，返回值转换失败 返回值不符合要求 errorMsg:{}", e.getMessage());
                return;
            }
            catch (Exception e)
            {
                logger.error("锐讯支付获得url，返回值转换失败", e.getMessage());
                return;
            }
            logger.info("锐讯支付获得payUrl 返回值 map:{}", contentMap);

            String payUrl = null;
            try
            {
                payUrl = convertPayUrl(contentMap.get("mwebUrl"));
            }
            catch (Exception e)
            {
                logger.error("锐讯支付获得url，转换支付url失败 e", e);
                return;
            }

            logger.info("锐讯支付 支付url：{}", payUrl);

            resp.sendRedirect(payUrl);

        }


    }

    @PostMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        Map<String, String[]> params = req.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        logger.info("/ruixun/callback param: " + queryString.substring(0, queryString.length() - 1));
        try {
            //Thread.sleep(20000);
            resp.getWriter().print("SUCCESS");
        } catch (IOException e) {
            throw new RuntimeException();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
        }
    }

    @PostMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {
        Map<String, String[]> params = req.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        logger.info("/ruixun/callback param: " + queryString.substring(0, queryString.length() - 1));
    }


    @GetMapping("withdraw")
    public String withdraw(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp) throws Exception
    {
        String url = "https://mpay.wxhang.cn/gateway";
        List<BasicNameValuePair> nvps = Lists.newArrayList();
        nvps.add(new BasicNameValuePair("appid", "mp_e402845fd900467f"));
        nvps.add(new BasicNameValuePair("requestNo", String.valueOf(System.currentTimeMillis())));
        nvps.add(new BasicNameValuePair("productId", "0201"));
        nvps.add(new BasicNameValuePair("transId", "07"));
        nvps.add(new BasicNameValuePair("orderDate", String.valueOf(System.currentTimeMillis()/1000)));
        nvps.add(new BasicNameValuePair("orderNo", String.valueOf(System.currentTimeMillis())));
        nvps.add(new BasicNameValuePair("returnUrl", "http://139.196.51.152:8080/ruixun/notify"));
        nvps.add(new BasicNameValuePair("notifyUrl", "http://139.196.51.152:8080/ruixun/callback"));
        nvps.add(new BasicNameValuePair("transAmt", String.valueOf(money)));
        nvps.add(new BasicNameValuePair("commodityName", "福利"));
        nvps.add(new BasicNameValuePair("merchantId", "282"));
        nvps.add(new BasicNameValuePair("signature", SignUtils.signData(nvps)));

        HttpResult httpResult = HttpsUtils.post(url, null, new UrlEncodedFormEntity(nvps, "UTF-8"), "UTF-8");

        return httpResult.toString();
    }

    private Map<String, String> convertResToMap(String content)
    {
        List<String> contentList = Splitter.on("&").omitEmptyStrings().splitToList(content);

        Map<String, String> contentMap = Maps.newHashMap();
        for (String _s : contentList)
        {
            List<String> _sList = Splitter.on("=").omitEmptyStrings().splitToList(_s);
            if(_sList.size() != 2)
            {
                throw new ValidationException(String.format("锐讯获得支付url 返回值异常 content:[%s], errorKeyValue:[%s]", content, _s));
            }
            contentMap.put(_sList.get(0), _sList.get(1));
        }

        return contentMap;
    }

    private String convertPayUrl(String rawPayUrl) throws UnsupportedEncodingException
    {
        String encodePayUrl = URLDecoder.decode(rawPayUrl, "UTF-8");
        int index = encodePayUrl.indexOf("?url=");
        String param = encodePayUrl.substring(index + 5);
        String decodeParam = URLEncoder.encode(param, "UTF-8");

        return encodePayUrl.substring(0, index+5) + decodeParam;
    }

}
