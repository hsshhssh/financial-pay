package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_platform")
public class PayPlatform {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 支付平台名称
     */
    @Column(name = "platform_name")
    private String platformName;

    /**
     * 平台编码 见配置
     */
    @Column(name = "platform_code")
    private String platformCode;

    /**
     * 支付方式 1微信wap 2支付宝wap
     */
    @Column(name = "pay_type")
    private Integer payType;

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