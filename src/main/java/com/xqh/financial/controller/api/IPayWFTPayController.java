package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2018/1/14.
 */
@RequestMapping("/xqh/financial/wft")
public interface IPayWFTPayController
{

    /**
     * 威富通异步回调地址
     * @param req
     * @param resp
     */
    @PostMapping("/pay/callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp);
}
