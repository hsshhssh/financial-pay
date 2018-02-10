package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_platform_wft_info")
public class PayPWI
{
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
     * 威富通商户id
     */
    @Column(name = "wft_mch_id")
    private String wftMchId;

    /**
     * 威富通key
     */
    @Column(name = "wft_key")
    private String wftKey;

    /**
     * 公众号appid
     */
    @Column(name = "wx_app_id")
    private String wxAppId;

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