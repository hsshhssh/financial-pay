package com.xqh.financial.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2018/1/10.
 */
@Controller
@RequestMapping("pingan/html")
@Slf4j
public class PingAnHtmlDemoController
{

    @RequestMapping("pay")
    public String pay(HttpServletRequest req, HttpServletResponse resp) {
        String tradeNo = req.getParameter("trade_no");
        String jumpurl = req.getParameter("jumpurl");
        String redirectUrl = "redirect:/pinganhtml/pay.html?trade_no=" + tradeNo + "&jumpurl=" + jumpurl;
        log.info("调整地址：{}", redirectUrl);
        return redirectUrl;
    }

}
