package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/7/2.
 */
@RequestMapping("/xqh/financial/vsp")
public interface IVSPPayController
{

    /**
     * 通联支付回调地址
     * @param req
     * @param resp
     */
    @RequestMapping("/pay/callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp);
}
