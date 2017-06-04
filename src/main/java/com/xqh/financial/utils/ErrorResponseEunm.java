package com.xqh.financial.utils;

/**
 * Created by hssh on 2017/6/4.
 */
public enum ErrorResponseEunm
{
    INVALID_PASSWORD(40000, "密码错误"),
    INVALID_USER(40001, "用户不存在"),
    DUPLICATE_USERNAME(40002, "用户名重复"),
    INVALID_METHOD_ARGS(40003, "参数校验失败");


    ErrorResponseEunm(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int status;
    public String msg;
}
