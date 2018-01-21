package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 新瑞云controller处理类
 * Created by hssh on 2018/1/19.
 */
@RequestMapping("/xqh/financial/xry")
public interface IXRYPayController
{
    /**
     * 新瑞云回调地址
     * @param req
     * @param resp
     */
    @RequestMapping("/callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp);
}
