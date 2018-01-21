package com.xqh.financial.utils.xry;

import lombok.Data;

/**
 * Created by hssh on 2018/1/19.
 */
@Data
public class XRYPayEntity
{
    private Integer money;

    private String ip;

    private String name;

    private String order;

    private String callbackUrl;

    private String notifyUrl;

    private String key;

    private String paraId;

    private String appId;

}
