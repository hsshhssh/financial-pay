package com.xqh.financial.utils;

import com.xqh.financial.utils.zkconf.Value;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 支付配置
 * Created by hssh on 2017/5/12.
 */
@Component
@Data
public class ConfigParamsUtils {

    /**
     * 当前服务器的ip+port
     */
    @Value(path = "/config/zkconf/xqhpay.conf", key = "zpayNotifyHost")
    private String  zpayNotifyHost;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "debugFlag")
    private String debugFlag;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "callbackValue")
    private String callbackValue;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "pinganOpenId")
    private String pinganOpenId;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "pinganOpenKey")
    private String pinganOpenKey;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "pinganUrl")
    private String pinganUrl;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "pinganPayType")
    private String pinganPayType;
}
