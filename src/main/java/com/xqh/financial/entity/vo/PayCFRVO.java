package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/5/31.
 */
@Data
public class PayCFRVO
{
    private Integer id;
    private Integer userId;
    private Integer appId;
    private String orderNo;
    private String userOrderNo;
    private Integer orderId;
    private Integer money;
    private String callbackUrl;
    private Integer state;
    private Integer successTime;
    private Integer lastCallTime;
    private Integer createTime;
    private Integer updateTime;

    private String userName;
    private String appName;
    private Double moneyYuan;
}
