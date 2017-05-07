package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_user_settlement")
public class PayUserSettlement {
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
     * 总金额
     */
    @Column(name = "total_money")
    private Integer totalMoney;

    /**
     * 总手续费
     */
    @Column(name = "total_handling_charge")
    private Integer totalHandlingCharge;

    /**
     * 应结总金额
     */
    @Column(name = "settlement_money")
    private Integer settlementMoney;

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