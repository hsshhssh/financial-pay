package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_user_playform_settlement")
public class PayUPS {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 支付平台编码
     */
    @Column(name = "playform_code")
    private String playformCode;

    /**
     * 订单总金额
     */
    @Column(name = "total_money")
    private Double totalMoney;

    /**
     * 手续费总额
     */
    @Column(name = "total_handling_charge")
    private Double totalHandlingCharge;

    /**
     * 应结总金额
     */
    @Column(name = "settlement_money")
    private Double settlementMoney;

    /**
     * 结算时间
     */
    @Column(name = "order_time")
    private Integer orderTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Integer createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Integer updateTime;
}