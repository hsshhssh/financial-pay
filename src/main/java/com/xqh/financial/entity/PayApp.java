package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_app")
public class PayApp {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id 对应pay_user主键id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 应用名称
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * 回调地址
     */
    @Column(name = "callback_url")
    private String callbackUrl;

    @Column(name = "nodify_url")
    private String nodifyUrl;

    /**
     * 应用秘钥
     */
    private String key;

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