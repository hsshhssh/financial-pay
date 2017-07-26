package com.xqh.financial.controller;

import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.TreeMap;

/**
 * Created by hssh on 2017/7/25.
 */
@RestController
@RequestMapping("weixin")
public class WeiXinDemoController
{
    private static final String appId = "wx721f5ed390bbeafe";
    private static final String secret = "9debe36021dfa26b227c0928b8864bbe";
    private static Logger logger = LoggerFactory.getLogger(WeiXinDemoController.class);

    @GetMapping("openid")
    public void getOpenId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

        String redirectUrl = "http://139.196.51.152:8080/weixin/openid/redirectUrl";

        String formatUrl = String.format(url, appId, URLEncoder.encode(redirectUrl,"UTF-8"));

        logger.info("weixin openid go to url:{}", formatUrl);

        resp.sendRedirect(formatUrl);
    }

    @GetMapping("/openid/redirectUrl")
    public String openIdReirectUrl(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        logger.info("param:{}", params);

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

        String formatUrl = String.format(url, appId, secret, params.get("code"));

        logger.info("get url:{}", formatUrl);

        HttpResult httpResult = HttpUtils.get(formatUrl);


        logger.info("get result:{}", httpResult);

        return httpResult.getContent();

    }

}
