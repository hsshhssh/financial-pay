package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/9/16.
 */
@Data
public class PayPlatformSettlementVO
{
    private Integer id;
    private Integer userId;
    private String playformCode;
    private Double totalMoney;
    private Double totalHandlingCharge;
    private Double settlementMoney;
    private Integer orderTime;
    private Integer createTime;
    private Integer updateTime;

    private String userName;
    private String platformName;
    private String platformEnName;
    private Double totalMoneyYuan;
    private Double totalHandlingChargeYuan;
    private Double settlementMoneyYuan;

}
