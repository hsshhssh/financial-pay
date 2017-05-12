package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_callback_fail_record")
public class PayCFR {
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
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 订单id
     */
    @Column(name = "order_id")
    private Integer orderId;

    /**
     * 金额（单位分）
     */
    private Integer money;

    /**
     * 回调地址
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    /**
     * 回调状态 1失败 2成功
     */
    private Integer state;

    /**
     * 回调成功时间
     */
    @Column(name = "success_time")
    private Integer successTime;

    /**
     * 最近回调时间
     */
    @Column(name = "last_call_time")
    private Integer lastCallTime;

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