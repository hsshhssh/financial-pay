package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IRuiXunPayController;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/7/11.
 */
@RestController
public class RuiXunPayController implements IRuiXunPayController
{
    @Override
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {

    }

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {

    }
}
