package com.xqh.financial.utils.vsp;

import com.xqh.financial.utils.zkconf.Value;
import lombok.Data;
import org.springframework.stereotype.Component;


/**
 * 通联支付配置
 * Created by hssh on 2017/7/2.
 */
@Component
@Data
public class VSPConfigParamUtils
{
    private static final String _path = "/config/zkconf/vsppay.conf";

    /**
     * 商户id
     */
    @Value(path = _path, key = "cusid")
    private String cusid;

    /**
     * 应用id
     */
    @Value(path = _path, key = "appid")
    private String appid;

    /**
     * key秘钥
     */
    @Value(path = _path, key = "key")
    private String key;


}
