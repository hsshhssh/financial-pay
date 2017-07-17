package com.xqh.financial.utils.ruixun;

import com.xqh.financial.utils.zkconf.Value;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Created by hssh on 2017/7/11.
 */
@Component
@Data
public class RuiXunConfigParamUtils
{

    @Value(path = "/config/zkconf/ruixinpay.conf", key = "appid")
    private String appid;



}
