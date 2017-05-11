package com.xqh.financial.utils;

/**
 * Created by hssh on 2017/5/7.
 */
public class Constant {

    /**
     * 支付成功
     */
    public static int RESULT_SUCCESS = 1;

    /**
     * 参数校验失败1001
     */
    public static int RESULT_INVALID_SIGN = 1001;

    /**
     * 链接超时失效1002
     */
    public static int RESULT_TIME_OUT = 1002;

    /**
     * 支付参数无效
     */
    public static int RESULT_INVALID_PARAM = 1004;

    /**
     * 用户取消支付
     */
    public static int RESULT_CANCEL_PAY= 1005;


    /**
     * 支付接口未知异常
     */
    public static int RESULT_UNKNOWN_ERROR = 1100;


    /**
     * 回调状态成功
     */
    public static int CALLBACK_SUCCESS = 1;

    /**
     * 回调状态失败
     */
    public static int CALLBACK_FAIL = 2;

}
