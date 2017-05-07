package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/5/7.
 */
@RequestMapping("/xqh/financial/zpay")
public interface IZPayController {

    /**
     * 掌易付支付结束后返回地址
     * @param result
     * @param req
     * @return
     */
    @RequestMapping("/nodifyUrl")
    public void nodifyUrl(@RequestParam(value="result", required = false) int result,
                         HttpServletRequest req,
                         HttpServletResponse resp);

    /**
     * 掌易付支付回调地址
     * @param req
     * @param resp
     */
    @RequestMapping("/pay/callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp);
}
