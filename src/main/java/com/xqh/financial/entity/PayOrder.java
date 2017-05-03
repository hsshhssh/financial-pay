package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_order")
public class PayOrder {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 应用id
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 订单金额 单位分
     */
    private Integer money;

    /**
     * 支付平台id
     */
    @Column(name = "platform_id")
    private Integer platformId;

    /**
     * 支付方式
     */
    @Column(name = "pay_type")
    private Integer payType;

    /**
     * 回调状态 1成功 2失败
     */
    @Column(name = "callback_state")
    private Integer callbackState;

    /**
     * 支付平台订单号
     */
    @Column(name = "platform_order_no")
    private String platformOrderNo;

    /**
     * 应扣手续费 单位分
     */
    @Column(name = "handling_charge")
    private Integer handlingCharge;

    /**
     * 回调成功时间
     */
    @Column(name = "callback_success_time")
    private Integer callbackSuccessTime;

    /**
     * 回调失败时间
     */
    @Column(name = "callback_fail_time")
    private Integer callbackFailTime;

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