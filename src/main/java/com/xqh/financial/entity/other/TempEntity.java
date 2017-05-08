package com.xqh.financial.entity.other;

import com.xqh.financial.utils.zkconf.Value;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Created by hssh on 2017/5/7.
 */
@Data
@Component
public class TempEntity {

    @Value(path = "/config/zkconf/xqhpay.conf", key = "notifyUrl")
    private String notifyUrl;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "callback")
    private String callback;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "secretKey")
    private String secretKey;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "qn")
    private String qn;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "appFeeName")
    private String appFeeName;

    @Value(path = "/config/zkconf/xqhpay.conf", key = "userId")
    private String userId;
}
