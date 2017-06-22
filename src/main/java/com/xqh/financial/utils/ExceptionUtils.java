package com.xqh.financial.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by hssh on 2017/6/4.
 */
@ControllerAdvice
public class ExceptionUtils
{
    private static Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public void handlerInvalidArgs(HttpServletResponse resp, MethodArgumentNotValidException e)
    {
        logger.warn("方法参数检验失败 msg:{}", e.getMessage());

        CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_METHOD_ARGS);
    }

}
