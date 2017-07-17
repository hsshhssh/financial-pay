package com.xqh.financial.service;

import com.google.common.base.Splitter;
import com.google.common.base.VerifyException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xqh.financial.entity.*;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.exception.ValidationException;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.mapper.PayOrderSerialMapper;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    @Autowired
    private PayOrderSerialMapper orderSerialMapper;

    @Autowired
    private PayAppMapper payAppMapper;


    private static Logger logger = LoggerFactory.getLogger(RuiXunPayService.class);

    public void pay(HttpServletResponse resp, int userId, int appId, int money, int orderSerial, int payType, PayApp payApp)
    {

        // 获得支付方式
        String productId = getPayType(payType);
        if(null == productId)
        {
            logger.error("锐讯支付 无支付通道 appId:{} userId:{} payType:{}", appId, userId, payType);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_NO_PAYTYPE);
            return ;
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
            return ;
        }


        // 获得支付url
        String payUrl = getPayUrl(orderSerial, productId, money, payApp.getAppName(), payPRXIList.get(0));

        if(StringUtils.isBlank(payUrl))
        {
            logger.error("锐讯支付 获得支付url失败 orderSerial:{} payUrl:{}", orderSerial, payUrl);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
            return;
        }

        try
        {
            resp.sendRedirect(payUrl);
        }
        catch (IOException e) {
            logger.error("锐讯支付 跳转支付url失败 orderSerial:{} payUrl:{}", orderSerial, payUrl);
            xqhPayService.notifyResult(resp, payApp.getNodifyUrl(), Constant.RESULT_UNKNOWN_ERROR);
            return ;

        }

    }

    /**
     * 取得支付url
     * @return
     */
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


    /**
     * 异步回调-校验参数
     * @return
     */
    public PayOrderSerial verifyCallbackParam(TreeMap<String, String> params)
    {
        // 取得订单流水信息
        PayOrderSerial orderSerial = null;
        String orderNo = params.get("orderNo");
        if(orderNo != null && StringUtils.isNumeric(orderNo))
        {
            orderSerial = orderSerialMapper.selectByPrimaryKey(Integer.valueOf(orderNo));
        }

        if(null == orderSerial)
        {
            logger.error("锐讯支付 异步回调 订单流水号异常 orderSerial:{} params：{}", orderNo, params);
            throw new VerifyException("锐讯支付 异步回调 订单流水号异常");
        }


        // 支付结果
        String respCode = params.get("respCode");
        if(!"0000".equals(respCode))
        {
            logger.error("锐讯支付 异步回调 支付结果非成功 respCode:{}, params:{}", respCode, params);
            throw new VerifyException("锐讯支付 异步回调 支付结果非成功");
        }

        // 锐讯参数
        String appid = params.get("appid");
        if(!ruixunConfig.getAppid().equals(appid))
        {
            logger.error("锐讯支付 异步回调 appid异常 appid:{}, params:{}", appid, params);
            throw new VerifyException("锐讯支付 异步回调 appid异常");
        }

        // TODO 检验签名
        return orderSerial;


    }


    /**
     * 创建订单
     */
    @Transactional
    public CallbackEntity insertOrderAndGenCallbackEntity(PayOrderSerial orderSerial, TreeMap<String, String> params)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        // 取得payApp
        PayApp payApp = payAppMapper.selectByPrimaryKey(orderSerial.getAppId());
        if(null == payApp)
        {
            logger.error("锐讯支付 异步回调 获得应用信息失败 appId:{} orderSerialId:{}", orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        logger.info("锐讯支付 异步回调 取得应用信息成功 appId:{}", payApp.getId());


        // 生成回调实体类
        CallbackEntity callbackEntity = xqhPayService.getCallbackByOrderSerial(orderSerial, payApp.getSecretkey());
        logger.info("锐讯支付 异步回调 生成回调实体类成功 callbackEntity:{}", callbackEntity);


        // 创建订单
        PayOrder payOrder = xqhPayService.insertOrderByOrderSerial(orderSerial, callbackEntity.getOrderNo(), params.get("payId"));
        if(null == payOrder)
        {
            logger.error("锐讯支付 异步回调 创建订单失败 appId:{} orderSerialId:{}", orderSerial.getAppId(), orderSerial.getId());
            return null;
        }
        callbackEntity.setOrderId(payOrder.getId());
        callbackEntity.setCallbackUrl(payApp.getCallbackUrl());
        logger.info("锐讯支付 异步回调 创建订单成功 appId:{}", payApp.getId());


        // 创建失败回调记录
        PayCFR payCFR = xqhPayService.insertCFR(orderSerial, callbackEntity, payOrder);
        callbackEntity.setCfrId(payCFR.getId());
        logger.info("锐讯支付 异步回调 创建失败的回调记录成功 appId:{}", payApp.getId());

        return callbackEntity;
    }


    /**
     * 异步回调商户
     */
    @Async
    public void callbackUser(CallbackEntity callbackEntity)
    {
        logger.info("锐讯支付 异步回调商户开始 orderId:{} appId:{}", callbackEntity.getOrderId(), callbackEntity.getAppId());
        String url = xqhPayService.genCallbackUrl(callbackEntity);
        logger.info("锐讯支付 回调商户url:{}", url);

        HttpResult httpResult = HttpUtils.get(url);

        logger.info("锐讯支付 回调商户返回值: {}", httpResult);

        // 根据回调结果修改订单状态
        xqhPayService.updateOrderStatus(httpResult, callbackEntity.getOrderId(), callbackEntity.getCfrId());

        logger.info("锐讯支付 回调商户异步操作结束 orderId:{}", callbackEntity.getOrderId());

    }
}
