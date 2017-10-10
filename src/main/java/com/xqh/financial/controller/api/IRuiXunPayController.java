package com.xqh.financial.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/7/11.
 */
@RequestMapping("/xqh/financial/ruixun")
public interface IRuiXunPayController
{

    /**
     * 锐讯支付返回地址
     * @param req
     * @param resp
     */
    @RequestMapping("/pay/notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp);


    /**
     * 锐讯支付异步回调地址
     * @param req
     * @param resp
     */
    @RequestMapping("/pay/callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp);


    @RequestMapping("/pay/refreshConfig")
    public void refreshConfig(HttpServletRequest req, HttpServletResponse resp);

}
