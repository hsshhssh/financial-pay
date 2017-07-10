package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_app_platform")
public class PayAppPlatform {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 应用id
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 支付平台id
     */
    @Column(name = "platform_id")
    private Integer platformId;

    /**
     * 平台编码
     */
    @Column(name = "platform_code")
    private String platformCode;

    /**
     * 支付方式 冗余字段
     */
    @Column(name = "pay_type")
    private Integer payType;

    /**
     * 状态 1启动 2禁用
     */
    private Integer state;

    /**
     * 手续费比例 单位万分之几
     */
    @Column(name = "interest_rate")
    private Integer interestRate;

    /**
     * 备注信息
     */
    private String remark;

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