package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/10/3.
 */
@Data
public class PayVerifyVO
{
    private Integer id;
    private Integer verifyTime;
    private Integer userId;
    private Double totalMoney;
    private Double settlementMoney;
    private Double transfer1;
    private Double transfer2;
    private Double transfer3;
    private Double transfer4;
    private Double transfer5;
    private Double transfer6;
    private Double diff;
    private String remark;
    private Integer createTime;
    private Integer updateTime;

    private String userName;
    private double totalMoneyYuan;
    private double settlementMoneyYuan;
    private double transfer1Yuan;
    private double transfer2Yuan;
    private double transfer3Yuan;
    private double transfer4Yuan;
    private double transfer5Yuan;
    private double transfer6Yuan;
    private double diffYuan;

}
