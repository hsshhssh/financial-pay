package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/6/2.
 */
@Data
public class PayAppVO
{
    private Integer id;
    private Integer userId;
    private String appName;
    private String callbackUrl;
    private String nodifyUrl;
    private String secretkey;
    private Integer createTime;
    private Integer updateTime;

    private String userName;
}
