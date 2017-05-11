package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_order_serial")
public class PayOrderSerial {
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
     * appId
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 支付方式
     */
    @Column(name = "pay_type")
    private Integer payType;

    /**
     * 支付金额
     */
    private Integer money;

    /**
     * 支付平台
     */
    @Column(name = "platform_id")
    private Integer platformId;

    /**
     * 用户订单号
     */
    @Column(name = "user_order_no")
    private String userOrderNo;

    /**
     * 用户自定义参数
     */
    @Column(name = "user_param")
    private String userParam;

    /**
     * 请求时间/300s 
     */
    @Column(name = "request_time")
    private Integer requestTime;

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