package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2018/2/10.
 */
@RequestMapping("/xqh/financial/jft")
public interface IJFTPayController
{
    /**
     * 金付通回调地址
     */
    @RequestMapping("/callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp);
}
