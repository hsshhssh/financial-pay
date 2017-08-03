package com.xqh.financial.entity.other;

import lombok.Data;

/**
 * Created by hssh on 2017/8/2.
 */
@Data
public class PayInfoEntity
{

    /**
     * 支付方式
     */
    private Integer payType;

    /**
     * 状态码
     */
    private String retCode;

    /**
     * 微信公众号支付信息
     */
    private String payInfo;

}
