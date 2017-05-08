package com.xqh.financial.entity.other;

import lombok.Data;

/**
 * Created by hssh on 2017/5/8.
 */
@Data
public class CallbackEntity {

    private String orderNo;
    private String payUserId;
    private Integer appId;
    private Integer payType;
    private String userParam;
    private String userOrderNo;
    private String sign;

}
