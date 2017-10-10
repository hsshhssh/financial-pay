package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_verify")
public class PayVerify
{
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 对账时间
     */
    @Column(name = "verify_time")
    private Integer verifyTime;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 总金额
     */
    @Column(name = "total_money")
    private Double totalMoney;

    /**
     * 应结金额
     */
    @Column(name = "settlement_money")
    private Double settlementMoney;

    /**
     * 转账金额1
     */
    private Double transfer1;

    /**
     * 转账金额2
     */
    private Double transfer2;

    /**
     * 转账金额3
     */
    private Double transfer3;

    /**
     * 转账金额4
     */
    private Double transfer4;

    /**
     * 转账金额5
     */
    private Double transfer5;

    /**
     * 转账金额6
     */
    private Double transfer6;

    /**
     * 差额
     */
    private Double diff;

    /**
     * 备注
     */
    private String remark;

    @Column(name = "create_time")
    private Integer createTime;

    @Column(name = "update_time")
    private Integer updateTime;
}