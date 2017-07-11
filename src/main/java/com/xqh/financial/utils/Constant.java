package com.xqh.financial.utils;

/**
 * Created by hssh on 2017/5/7.
 */
public class Constant {

    /**
     * 支付回调商户返回的正确值
     */
    public static String CALLBACK_SUCCESS_RESULT = "1";

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
     * 无支付通道
     */
    public static int RESULT_NO_PAYTYPE = 1003;

    /**
     * 支付参数无效
     */
    public static int RESULT_INVALID_PARAM = 1004;

    /**
     * 用户取消支付
     */
    public static int RESULT_CANCEL_PAY= 1005;


    /**
     * 订单重复支付
     */
    public static int RESULT_REPEAT_PAY= 1006;


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


    /**
     * 启动状态
     */
    public static int ENABLE_STATE = 1;


    /**
     * 禁用
     */
    public static int DISABLE_STATE = 2;

    /**
     * 成功状态
     */
    public static int SUCCESS_STATE = 1;


    /**
     * 失败状态
     */
    public static int FAIL_STATE = 2;


    /**
     * 掌易付支付通道
     */
    public static String ZPAY_CHANNEL_CODE = "0001";

    /**
     * 通联支付通道
     */
    public static String VSP_CHANNEL_CODE = "0002";

    /**
     * 锐讯支付通道
     */
    public static String RUIXUN_CHANNEL_CODE = "0003";

    /**
     * 微信wap支付方式
     */
    public final static int WXWAP_PAY_TYPE = 1;

    /**
     * 支付宝wap支付方式
     */
    public final static int ALIPAYWAP_PAY_TYPE = 2;

}
