package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "pay_platform_ruixun_info")
public class PayPRXI
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
     * 手续费
     */
    @Column(name = "interest_rate")
    private Integer interestRate;

    /**
     * 锐讯appid
     */
    @Column(name = "ruixin_app_id")
    private String ruixinAppId;

    /**
     * 私钥证书zk路径
     */
    @Column(name = "private_certificate_path")
    private String privateCertificatePath;

    /**
     * 公钥证书zk路径
     */
    @Column(name = "public_certificate_path")
    private String publicCertificatePath;

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