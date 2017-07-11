package com.xqh.financial.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayPRXI;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.mapper.PayPRXIMapper;
import com.xqh.financial.utils.*;
import com.xqh.financial.utils.ruixun.RuiXunConfigParamUtils;
import com.xqh.financial.utils.ruixun.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 锐讯支付逻辑层
 * Created by hssh on 2017/7/11.
 */
@Service
public class RuiXunPayService
{
    @Autowired
    private RuiXunConfigParamUtils ruixunConfig;

    @Autowired
    private ConfigParamsUtils config;

    @Autowired
    private PayPRXIMapper payPRXIMapper;

    @Autowired
    private XQHPayService xqhPayService;

    private static Logger logger = LoggerFactory.getLogger(RuiXunPayService.class);

    public void pay(HttpServletResponse resp, int userId, int appId, int money, int orderSerial, int payType, PayApp payApp)
    {

        // 获得支付方式
        String productId = getPayType(payType);
        if(null == productId)
        {
            logger.error("锐讯支付 无支付通道 appId:{} userId:{} payType:{}", appId, userId, payType);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
        }

        // 获取锐讯支付平台信息
        Search search = new Search();
        search.put("userId_eq", userId);
        Example example = new ExampleBuilder(PayPRXI.class).search(search).build();
        List<PayPRXI> payPRXIList = payPRXIMapper.selectByExample(example);
        if(payPRXIList.size() != 1)
        {
            logger.error("锐讯支付 支付通道异常 payPRXIList.size:{}", payPRXIList.size());
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
        }


        // 获得支付url
        String payUrl = getPayUrl(orderSerial, productId, money, payApp.getAppName(), payPRXIList.get(0));

        if(StringUtils.isBlank(payUrl))
        {
            logger.error("锐讯支付 获得支付url失败 orderSerial:{} payUrl:{}", orderSerial, payUrl);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
        }

        try
        {
            resp.sendRedirect(payUrl);
        }
        catch (IOException e) {
            logger.error("锐讯支付 跳转支付url失败 orderSerial:{} payUrl:{}", orderSerial, payUrl);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);

        }


    }


    private String getPayUrl(int orderSerial, String productId, int money, String appName, PayPRXI payPRXI)
    {


        String url = "https://mpay.wxhang.cn/gateway";
        List<BasicNameValuePair> nvps = Lists.newArrayList();
        nvps.add(new BasicNameValuePair("appid", ruixunConfig.getAppid()));
        nvps.add(new BasicNameValuePair("requestNo", String.valueOf(orderSerial)));
        nvps.add(new BasicNameValuePair("productId", productId));
        nvps.add(new BasicNameValuePair("transId", "10")); // 接口类型 统一下单 10
        nvps.add(new BasicNameValuePair("orderDate", String.valueOf(System.currentTimeMillis()/1000))); // 应用场境需要转成 精确到 秒的时间戳
        nvps.add(new BasicNameValuePair("orderNo", String.valueOf(orderSerial)));
        nvps.add(new BasicNameValuePair("returnUrl", config.getZpayNotifyHost().trim() + "/xqh/financial/ruixun/pay/notify"));
        nvps.add(new BasicNameValuePair("notifyUrl", config.getZpayNotifyHost().trim() + "/xqh/financial/ruixun/pay/callback"));
        nvps.add(new BasicNameValuePair("transAmt", String.valueOf(money)));
        nvps.add(new BasicNameValuePair("commodityName", appName));
        nvps.add(new BasicNameValuePair("merchantId", payPRXI.getRuixinMerchantid()));
        nvps.add(new BasicNameValuePair("storeId", payPRXI.getRuixinStoreid()));

        String sign;
        try
        {
             sign = SignUtils.signData(nvps);
        } catch (Exception e)
        {
            logger.error("锐讯支付 加密信息异常 orderSerial:{}, e:{}", orderSerial, e.getMessage());
            return null;

        }
        nvps.add(new BasicNameValuePair("signature", sign));

        HttpResult httpResult = null;
        try
        {
            httpResult = HttpsUtils.post(url, null, new UrlEncodedFormEntity(nvps, "UTF-8"), "UTF-8");
        }
        catch (Exception e) {
            logger.error("锐讯支付 post请求获取支付地址异常 orderSerial:{}, e:{}", orderSerial, e.getMessage());
            return null;
        }

        if(httpResult.getStatus() != 200)
        {
            logger.error("锐讯支付 post请求获取支付地址异常 status!=200 orderSerial:{} httpConent:{}", orderSerial, httpResult);
            return null;
        }
        else
        {
            boolean signFlag = SignUtils.verferSignData(httpResult.getContent());
            if (!signFlag)
            {
                logger.error("锐讯支付 验签失败 orderSerial:{} httpResult:{}", orderSerial, httpResult);
                return null;
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
                return null;
            }
            catch (Exception e)
            {
                logger.error("锐讯支付获得url，返回值转换失败", e.getMessage());
                return null;
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
                return null;
            }

            logger.info("锐讯支付 支付url：{}", payUrl);
            return payUrl;
        }
    }

    /**
     * 获得支付方式
     * @param payType 新企航支付方式
     * @return
     */
    private String getPayType(int payType)
    {
        if(Constant.WXWAP_PAY_TYPE == payType)
        {
            // 微信wap支付
            return "0107";
        }
        else
        {
            return null;
        }
    }


    /**
     * http返回结果转为map
     * @param content
     * @return
     */
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


    /**
     * url编码
     * @param rawPayUrl
     * @return
     * @throws UnsupportedEncodingException
     */
    private String convertPayUrl(String rawPayUrl) throws UnsupportedEncodingException
    {
        String encodePayUrl = URLDecoder.decode(rawPayUrl, "UTF-8");
        int index = encodePayUrl.indexOf("?url=");
        String param = encodePayUrl.substring(index + 5);
        String decodeParam = URLEncoder.encode(param, "UTF-8");

        return encodePayUrl.substring(0, index+5) + decodeParam;
    }


}
