package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/5/7.
 */
@RequestMapping("/xqh/financial")
public interface IXQHPayController {

    /**
     * 支付接口
     * @param req
     * @param resp
     */
    @RequestMapping("/pay")
    public void pay(HttpServletRequest req, HttpServletResponse resp);


}
