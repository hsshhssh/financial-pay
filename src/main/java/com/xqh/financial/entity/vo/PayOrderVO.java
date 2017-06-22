package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/5/16.
 */
@Data
public class PayOrderVO
{

    private Integer id;
    private String orderNo;
    private Integer orderSerial;
    private String userOrderNo;
    private Integer userId;
    private Integer appId;
    private Integer money;
    private Integer platformId;
    private Integer payType;
    private Integer callbackState;
    private String platformOrderNo;
    private Integer interestRate;
    private Integer callbackSuccessTime;
    private Integer callbackFailTime;
    private Integer createTime;
    private Integer updateTime;

    private String payTypeStr;
    private String callbackStateStr;
    private String userName;
    private Double moneyYuan;
    private Double interestRatePrecent;

}
