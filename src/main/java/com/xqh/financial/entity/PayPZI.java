package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_platform_zpay_info")
public class PayPZI {
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
     * 应用id
     */
    @Column(name = "app_id")
    private Integer appId;

    /**
     * 掌易付商户id
     */
    @Column(name = "zpay_parent_id")
    private String zpayParentId;

    /**
     * 掌易付appId
     */
    @Column(name = "zpay_app_id")
    private Integer zpayAppId;

    /**
     * 掌易付应用key
     */
    @Column(name = "zpay_key")
    private String zpayKey;

    /**
     * 掌易付渠道编码
     */
    @Column(name = "zpay_qn")
    private String zpayQn;

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
    @Column(name = "upadte_time")
    private Integer upadteTime;
}