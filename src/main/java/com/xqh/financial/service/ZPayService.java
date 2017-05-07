package com.xqh.financial.service;

import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.entity.other.TempEntity;
import com.xqh.financial.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TempEntity tempEntity;

    /**
     * 掌易付支付接口
     */
    public void pay(HttpServletResponse resp,  int userId, int appId, int money, int userOrderNo) {
        String partnerId = String.valueOf(userId);
        //String currency = "1000200010000000";
        String times = CommonUtils.getFormatDate("yyyyMMddHHmmss");
        //String secretKey = "A3F4A7E77AD7474E9105AD5B7DFB8240";

        String sign = CommonUtils.getMd5(partnerId + appId + tempEntity.getCurrency() + money+times + tempEntity.getSecretKey());

        StringBuffer sb = new StringBuffer();
        sb.append("http://pay.csl2016.cn:8000/sp/theThirdPayWapEntrance.e?");
        sb.append("partnerId=" + partnerId +"&");
        sb.append("appId="+ appId +"&");
        sb.append("money="+ money +"&");
        sb.append("qn=" + tempEntity.getQn() + "&");
        sb.append("currency="+ tempEntity.getCurrency() +"&");
        sb.append("sign=" + sign + "&");
        sb.append("cpparam=" + userOrderNo + "&");
        sb.append("notifyUrl=http://139.196.51.152:8080/nodifyUrl&");
        String name = null;
        try {
            name = java.net.URLEncoder.encode(tempEntity.getAppFeeName(),"utf-8");
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

    public CallbackEntity genCallbackEntity(HttpServletRequest resp) {
        CallbackEntity callbackEntity = new CallbackEntity();
        callbackEntity.setOrderNo(resp.getParameter("orderid"));
        callbackEntity.setPayUserId(Integer.valueOf(tempEntity.getUserId()));
        callbackEntity.setAppId(Integer.valueOf(resp.getParameter("appId")));
        callbackEntity.setPayType(1); // 暂时只有微信wap支付
        callbackEntity.setUserParam("1");// 目前只传1
        callbackEntity.setUserOrderNo(resp.getParameter("cporderid"));


        String sign = CommonUtils.getMd5(callbackEntity.getOrderNo() + callbackEntity.getPayUserId() + callbackEntity.getAppId() + callbackEntity.getPayType() + tempEntity.getSecretKey());
        callbackEntity.setSign(sign);

        return callbackEntity;
    }

}
