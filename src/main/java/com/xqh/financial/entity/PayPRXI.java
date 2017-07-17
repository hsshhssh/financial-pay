package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_platform_ruixun_info")
public class PayPRXI {
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
     * 锐讯支付平台商户编号
     */
    @Column(name = "ruixin_merchantId")
    private String ruixinMerchantid;

    /**
     * 锐讯支付平台门店编号
     */
    @Column(name = "ruixin_storeId")
    private String ruixinStoreid;

    /**
     * 备注
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