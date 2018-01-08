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

import static com.xqh.financial.utils.pingan.HttpsUtil.getParamStr;

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
        String postmapStr = HttpsUtil.getParamStr(postmap);
        log.info("请求参数:{}", postmapStr);
        StringEntity stringEntity = new StringEntity(getParamStr(postmap), "UTF-8");
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

    @GetMapping("paylist")
    public void payList(HttpServletRequest req, HttpServletResponse resp) {
        // 初始化参数
        String pmtType   = "0,1,2,3";
        String timestamp = new Date().getTime() / 1000 + "";    // 时间

        try {
            // 固定参数
            TreeMap<String, String> postmap = new TreeMap<String, String>();//请求参数的map
            postmap.put("open_id", TestParams.OPEN_ID);
            postmap.put("timestamp", timestamp);

            TreeMap<String, String> datamap = new TreeMap<String, String>();//data参数的map
            datamap.put("pmt_type", pmtType);

            /**
             * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
             */
            TLinx2Util.handleEncrypt(datamap, postmap);

            /**
             * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
             */
            TLinx2Util.handleSign(postmap);

            /**
             * 3 请求、响应
             */
            String postmapStr = HttpsUtil.getParamStr(postmap);
            log.info("请求参数:{}", postmapStr);
            StringEntity stringEntity = new StringEntity(postmapStr, "UTF-8");
            HttpResult post = HttpsUtils.post(TestParams.OPEN_URL + TestParams.PAYLIST, null, stringEntity, "UTF-8");
            log.info("响应结果：{}", unicodeToString(post.getContent()));

        } catch (Exception e) {
            e.printStackTrace();
        }
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