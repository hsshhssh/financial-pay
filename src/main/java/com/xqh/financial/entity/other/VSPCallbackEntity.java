package com.xqh.financial.entity.other;

import lombok.Data;

/**
 * 通联支付回调实体类
 * Created by hssh on 2017/7/2.
 */
@Data
public class VSPCallbackEntity
{
    private String trxid;
    private String trxdate;
    private String termauthno;
    private String termtraceno;
    private String trxstatus;
    private String termrefnum;
    private String appid;
    private String trxcode;
    private String cusid;
    private String paytime;
    private String sign;
    private String acct;
    private String cusorderid;
    private String chnltrxid;
    private String trxreserved;
    private String outtrxid;
    private String trxamt;


    private String termno;
    private String termbatchid;
    private String srctrxid;
}
