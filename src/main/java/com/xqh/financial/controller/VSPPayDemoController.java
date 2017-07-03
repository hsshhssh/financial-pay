package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.vsp.HttpConnectionUtil;
import com.xqh.financial.utils.vsp.SybUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hssh on 2017/6/19.
 */
//@RestController
@Controller
@RequestMapping("vsp")
public class VSPPayDemoController
{
    private static Logger logger = LoggerFactory.getLogger(VSPPayDemoController.class);

    @GetMapping("pay/html")
    public void payHtml(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp)
    {
        try
        {
            String payUrl = getPayUrl(money);

            req.setAttribute("payUrl", payUrl);
            req.getRequestDispatcher("/vsp/html").forward(req, resp);
        }
        catch (ServletException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @GetMapping("html")
    public String html(HttpServletRequest req, HttpServletResponse resp, ModelMap modelMap)
    {

        modelMap.addAttribute("payUrl", req.getAttribute("payUrl"));

        return "index";
    }


    @GetMapping("pay")
    @ResponseBody
    public void payJsp(HttpServletRequest req, HttpServletResponse resp, @RequestParam("money") int money)
    {

        String payUrl = getPayUrl(money);

        try
        {
            resp.sendRedirect(payUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PostMapping("callback")
    @ResponseBody
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = SybUtil.getParams(req);
        logger.info("params:{}", JSONObject.toJSON(params));

        CommonUtils.writeResponse(resp, "success");
    }

    @PostMapping("notify")
    @ResponseBody
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
        logger.info("/mch/notify param: " + queryString.substring(0, queryString.length() - 1));
    }

    public static String getPayUrl(int money)
    {
        try {
            HttpConnectionUtil http = new HttpConnectionUtil("https://vsp.allinpay.com/apiweb/unitorder"+"/pay");
            http.init();
            TreeMap<String, String> params = Maps.newTreeMap();

            params.put("cusid", "335581048165186");
            params.put("appid", "00010835");
            params.put("version", "11");
            params.put("trxamt", String.valueOf(money));
            params.put("reqsn", String.valueOf(System.currentTimeMillis()));
            params.put("paytype", "A01");
            params.put("randomstr", SybUtil.getValidatecode(8));
            params.put("body", "测试应用");
            params.put("remark", "mark");
            //params.put("acct", acct);
            params.put("notify_url", "http://139.196.51.152:8080/vsp/callback");
            params.put("sign", SybUtil.sign(params,"gzxqhpay1qa@WS#ED"));

            byte[] bys = http.postParams(params, true);
            String result = new String(bys,"UTF-8");
            Map<String,String> map = SybUtil.handleResult(result);
            String payUrl = map.get("payinfo");

            return payUrl;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("checkstand")
    public String checkstand()
    {
        return "checkstand";
    }

}
