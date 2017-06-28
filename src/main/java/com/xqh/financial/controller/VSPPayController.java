package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.vsp.HttpConnectionUtil;
import com.xqh.financial.utils.vsp.SybUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hssh on 2017/6/19.
 */
@RestController
@RequestMapping("vsp")
public class VSPPayController
{
    private static Logger logger = LoggerFactory.getLogger(VSPPayController.class);

    //@GetMapping("pay")
    //public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp)
    //{
    //    try
    //    {
    //        //req.setAttribute("test", 123);
    //        req.getRequestDispatcher("/vsp/pay/html").forward(req, resp);
    //    }
    //    catch (ServletException e)
    //    {
    //        e.printStackTrace();
    //    } catch (IOException e)
    //    {
    //        e.printStackTrace();
    //    }
    //}

    @GetMapping("pay")
    public void payJsp(HttpServletRequest req, HttpServletResponse resp, @RequestParam("money") int money) throws Exception {

        HttpConnectionUtil http = new HttpConnectionUtil("https://vsp.allinpay.com/apiweb/unitorder"+"/pay");
        http.init();
        TreeMap<String, String> params = Maps.newTreeMap();

        params.put("cusid", "335581048165186");
        params.put("appid", "00010835");
        params.put("version", "11");
        params.put("trxamt", String.valueOf(money));
        params.put("reqsn", "2");
        params.put("paytype", "A01");
        params.put("randomstr", SybUtil.getValidatecode(8));
        params.put("body", "测试应用");
        params.put("remark", "mark");
        //params.put("acct", acct);
        params.put("notify_url", "www.baidu.com");
        params.put("sign", SybUtil.sign(params,"gzxqhpay1qa@WS#ED"));

        byte[] bys = http.postParams(params, true);
        String result = new String(bys,"UTF-8");
        Map<String,String> map = SybUtil.handleResult(result);
        String payUrl = map.get("payinfo");

        resp.sendRedirect(payUrl);
    }


    @PostMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = SybUtil.getParams(req);
        logger.info("params:{}", JSONObject.toJSON(params));

        CommonUtils.writeResponse(resp, "success");
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
        logger.info("/mch/notify param: " + queryString.substring(0, queryString.length() - 1));
    }

}
