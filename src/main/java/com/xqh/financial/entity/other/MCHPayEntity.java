package com.xqh.financial.entity.other;

import lombok.Data;

/**
 * Created by hssh on 2017/6/21.
 */
@Data
public class MCHPayEntity
{
    private String funcode;
    private String appId;
    private String mhtOrderNo;
    private String mhtOrderName;
    private String version;
    private String mhtCurrencyType;
    private String mhtOrderAmt;
    private String mhtOrderDetail;
    private String mhtOrderType;
    private String mhtOrderStartTime;
    private String notifyUrl;
    private String frontNotifyUrl;
    private String mhtCharset;
    private String deviceType;
    private String outputType;
    private String mhtReserved;
    private String payChannelType;
    private String mhtSignType;
    private String payAccNo;
    private String mhtSignature;



}
