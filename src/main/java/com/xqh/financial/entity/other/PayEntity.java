package com.xqh.financial.entity.other;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/5/7.
 */
@Data
public class PayEntity {

    @Min(1)
    @NotNull
    private Integer userId;

    @Min(1)
    @NotNull
    private Integer appId;

    @Min(1)
    @NotNull
    private Integer money;

    @Min(1)
    @NotNull
    private Integer time;

    @Min(1)
    @Max(1)
    private Integer payType;

    @Length(min = 1, max = 32)
    private String sign;

    @Length(max = 50)
    private String userOrderNo;

    @Length(max = 255)
    private String userParam;

    /**
     * 订单流水号
     */
    private Integer orderSerial;

    /**
     * 支付平台
     */
    private Integer platformId;

}
