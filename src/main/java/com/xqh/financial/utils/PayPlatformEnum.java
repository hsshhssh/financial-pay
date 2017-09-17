package com.xqh.financial.utils;

/**
 * Created by hssh on 2017/9/16.
 */
public enum PayPlatformEnum
{
    ZPAY("0001", "掌易付", "zyf"),
    VSPPAY("0002", "通联支付", "tl"),
    RUIXUNPAY("0003", "锐讯支付", "rx"),

    ;
    private String code;
    private String name;
    private String enName;

    PayPlatformEnum(String code, String name, String enName)
    {
        this.code = code;
        this.name = name;
        this.enName = enName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEnName()
    {
        return enName;
    }

    public void setEnName(String enName)
    {
        this.enName = enName;
    }
}
