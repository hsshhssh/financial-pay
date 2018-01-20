package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_platform_xry_info")
public class PayPXI
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
     * 新瑞云para_id
     */
    @Column(name = "xry_para_id")
    private String xryParaId;

    /**
     * 新瑞云app_id
     */
    @Column(name = "xry_app_id")
    private String xryAppId;

    /**
     * 新瑞云秘钥
     */
    @Column(name = "xry_key")
    private String xryKey;

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