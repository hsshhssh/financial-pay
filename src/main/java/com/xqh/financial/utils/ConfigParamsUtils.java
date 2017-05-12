package com.xqh.financial.utils;

import com.xqh.financial.utils.zkconf.Value;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Created by hssh on 2017/5/12.
 */
@Component
@Data
public class ConfigParamsUtils {

    @Value(path = "/config/zkconf/xqhpay.conf", key = "zpayNotifyHost")
    private String zpayNotifyHost;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "debugFlag")
    private String debugFlag;
}
