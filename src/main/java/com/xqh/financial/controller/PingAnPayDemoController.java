package com.xqh.financial.controller;

import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ConfigParamsUtils;
import com.xqh.financial.utils.pingan.TLinx2Util;
import com.xqh.financial.utils.pingan.TLinxAESCoder;
import com.xqh.financial.utils.pingan.TestParams;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
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
        postmap.put("open_id", configParamsUtils.getPinganOpenId().trim());
        postmap.put("timestamp", timestamp);

        TreeMap<String, Object> datamap = new TreeMap<>();


        datamap.put("notify_url", configParamsUtils.getZpayNotifyHost().trim() + "/pingan/callback");
        datamap.put("original_amount", String.valueOf(money));
        datamap.put("trade_amount", String.valueOf(money));
        datamap.put("ord_name", "ordName");
        datamap.put("out_no", String.valueOf(System.currentTimeMillis()));
        datamap.put("spbill_create_ip", CommonUtils.getIp(req));
        datamap.put("trade_type", "MWEB");

        TreeMap<String, Object> scene_info = new TreeMap<>();
        TreeMap<String, Object> h5_info = new TreeMap<>();
        h5_info.put("type", "Wap");
        h5_info.put("wap_url", "https://pay.qq.com");
        h5_info.put("wap_name", "腾讯充值");
        scene_info.put("h5_info", h5_info);

        datamap.put("scene_info", scene_info);
        datamap.put("pmt_tag", "WeixinOL");


        //datamap.put("out_no", String.valueOf(System.currentTimeMillis()));
        //datamap.put("pmt_tag", configParamsUtils.getPinganPayType().trim());
        //datamap.put("pmt_name", "微信");
        //datamap.put("ord_name", "ordName");
        //datamap.put("original_amount", String.valueOf(money));
        ////datamap.put("discount_amount", discountAmount+"");
        ////datamap.put("ignore_amount", ignoreAmount+"");
        //datamap.put("trade_amount", String.valueOf(money));
        ////datamap.put("trade_account", tradeAccount);
        //datamap.put("trade_no", "tradeNo" + System.currentTimeMillis());
        //datamap.put("remark", "remark");
        //datamap.put("tag", "tag");
        //datamap.put("notify_url", configParamsUtils.getZpayNotifyHost().trim() + "/pingan/callback");
        String jumpurl = configParamsUtils.getZpayNotifyHost().trim() + "/pingan/notify";
        ////datamap.put("jump_url", jumpurl);
        ////datamap.put("sub_appid", "sub_appid");
        //datamap.put("trade_type", "MWEB");
        //datamap.put("spbill_create_id", CommonUtils.getIp(req));
        //datamap.put("scene_info", "senceInfo");


        /**
         * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
         */
        TLinx2Util.handleEncrypt(datamap, postmap, configParamsUtils.getPinganOpenKey().trim());

        /**
         * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
         */
        TLinx2Util.handleSign(postmap, configParamsUtils.getPinganOpenKey().trim());

        /**
         * 3 请求、响应
         */
        String rspStr = TLinx2Util.handlePost(postmap, TestParams.PAYORDER, configParamsUtils.getPinganUrl().trim());
        log.info("====post响应字符串= " + rspStr);

        /**
         * 4 验签  有data节点时才验签
         */
        JSONObject respObject = JSONObject.fromObject(rspStr);
        Object dataStr    = respObject.get("data");

        if (!rspStr.isEmpty() && ( dataStr != null )) {
            if (TLinx2Util.verifySign(respObject, configParamsUtils.getPinganOpenKey().trim())) {    // 验签成功

                /**
                 * 5 AES解密，并hex2bin
                 */
                String respData = TLinxAESCoder.decrypt(dataStr.toString(), configParamsUtils.getPinganOpenKey().trim());

                JSONObject jsonObject = JSONObject.fromObject(respData);
                log.info("=================响应data内容:{}", jsonObject);

                resp.sendRedirect("/pingan/html/pay?trade_no=" + jsonObject.get("trade_no") + "&jumpurl=" + jumpurl);

            } else {
                System.out.println("==========验签失败==========");
            }
        } else {
            log.info("没有返回data数据 返回数据：{}", unicodeToString(rspStr));
        }
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
            postmap.put("open_id", configParamsUtils.getPinganOpenId().trim());
            postmap.put("timestamp", timestamp);

            TreeMap<String, Object> datamap = new TreeMap<>();//data参数的map
            datamap.put("pmt_type", pmtType);

            /**
             * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
             */
            TLinx2Util.handleEncrypt(datamap, postmap, configParamsUtils.getPinganOpenKey());

            /**
             * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行md5加密(小写)，得到签名
             */
            TLinx2Util.handleSign(postmap, configParamsUtils.getPinganOpenKey());

            /**
             * 3 请求、响应
             */

            String rspStr = TLinx2Util.handlePost(postmap, TestParams.PAYLIST, configParamsUtils.getPinganUrl().trim());
            log.info("响应参数：{}", unicodeToString(rspStr));

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