package com.xqh.financial.controller;

import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.HttpsUtils;
import com.xqh.financial.utils.pingan.HttpsUtil;
import com.xqh.financial.utils.pingan.TLinx2Util;
import com.xqh.financial.utils.pingan.TestParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.StringEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hssh on 2018/1/4.
 */
@RestController
@RequestMapping("pingan")
@Slf4j
public class PingAnPayDemoController
{
    @Resource
    private ConfigParamsUtils configParamsUtils;

    @GetMapping("pay")
    public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp) throws Exception
    {

        TreeMap<String, String> postmap = new TreeMap<String, String>();
        String timestamp = new Date().getTime() / 1000 + "";
        postmap.put("open_id", TestParams.OPEN_ID);
        postmap.put("timestamp", timestamp);

        TreeMap<String, String> datamap = new TreeMap<String, String>();

        datamap.put("out_no", String.valueOf(System.currentTimeMillis()));
        datamap.put("pmt_tag", "Weixin");
        datamap.put("pmt_name", "微信");
        datamap.put("ord_name", "ordName");
        datamap.put("original_amount", String.valueOf(money));
        //datamap.put("discount_amount", discountAmount+"");
        //datamap.put("ignore_amount", ignoreAmount+"");
        datamap.put("trade_amount", String.valueOf(money));
        //datamap.put("trade_account", tradeAccount);
        //datamap.put("trade_no", tradeNo);
        datamap.put("remark", "remark");
        datamap.put("tag", "tag");
        datamap.put("notify_url", configParamsUtils.getZpayNotifyHost().trim() + "/pingan/callback");
        datamap.put("jump_url", configParamsUtils.getZpayNotifyHost().trim() + "/pingan/notify");
        //datamap.put("sub_appid", "sub_appid");
        //datamap.put("sub_openid", "sub_openid");
        datamap.put("JSAPI", "1");
        datamap.put("trade_type", "MWEB");

        TLinx2Util.handleEncrypt(datamap, postmap);

        TLinx2Util.handleSign(postmap);

        //String rspStr = TLinx2Util.handlePost(postmap, TestParams.PAYORDER);
        StringEntity stringEntity = new StringEntity(HttpsUtil.getParamStr(postmap), "UTF-8");
        HttpResult post = HttpsUtils.post(TestParams.OPEN_URL + TestParams.PAYORDER, null, stringEntity, "UTF-8");
        log.info("post response:{}", unicodeToString(post.getContent()));
    }

    @PostMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp) {

    }

    @GetMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp) {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("notify params:{}", params);

        CommonUtils.writeResponse(resp, params);
        return;
    }

    public static void main(String[] args)
    {
        System.out.println(unicodeToString("token\\u4e0d\\u80fd\\u4e3a\\u7a7a"));
    }

    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }
}