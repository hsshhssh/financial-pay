package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/5/28.
 */
@Data
public class PayAppSettlementVO
{
    private Integer id;
    private Integer userId;
    private Integer appId;
    private Double totalMoney;
    private Double totalHandlingCharge;
    private Double settlementMoney;
    private Integer orderTime;
    private Integer createTime;
    private Integer updateTime;

    private String userName;
    private Double totalMoneyYuan;
    private Double totalHandlingChargeYuan;
    private Double settlementMoneyYuan;


}
