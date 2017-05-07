package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_user_param")
public class PayUserParam {
    /**
     * 主键id&&订单流水号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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