package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.xry.BeiBaoFuPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by hssh on 2018/1/19.
 */
@RestController
@RequestMapping("xry")
@Slf4j
public class XRYPayDemoController
{
    @Resource
    private BeiBaoFuPay beiBaoFuPay;

    @GetMapping("pay")
    public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String url = beiBaoFuPay.wechatPay(String.valueOf(money), "2", CommonUtils.getIp(req), "name", String.valueOf(System.currentTimeMillis()));

        log.info("xry payUrl:{}", url);
        resp.sendRedirect(url);
    }

    @GetMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("callback param:{}", JSONObject.toJSON(params));
        CommonUtils.writeResponse(resp, "Ok");
    }

    @GetMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        log.info("notify param:{}", JSONObject.toJSON(params));
    }


}
