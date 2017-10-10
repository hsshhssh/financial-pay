package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/10/8.
 */
@Data
public class PayPlatformVO
{
    private Integer id;
    private String platformName;
    private String platformCode;
    private Integer payType;
    private Integer createTime;
    private Integer updateTime;

}
