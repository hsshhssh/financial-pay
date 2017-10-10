package com.xqh.financial.utils.ruixun;

import com.github.zkclient.ZkClient;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.xqh.financial.entity.PayPRXI;
import com.xqh.financial.mapper.PayPRXIMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/9/28.
 */
@Component
public class CertificateUtils
{
    @Autowired
    private PayPRXIMapper payPRXIMapper;

    @Autowired
    private ZkClient zkClient;

    private static Map<String, String> map;

    // 可能是tk的问题 利用spring的初始化方法中调用tk的方法报错
    //@PostConstruct
    //public void init()
    //{
    //    initMap();
    //}

    public void initMap()
    {
        Map<String, String> tempMap = Maps.newHashMap();
        Example example = new ExampleBuilder(PayPRXI.class).search(new Search()).build();
        List<PayPRXI> payPRXIList = payPRXIMapper.selectByExample(example);

        for (PayPRXI payPRXI : payPRXIList)
        {
            if(StringUtils.isNotBlank(payPRXI.getPublicCertificatePath()))
            {
                tempMap.put(payPRXI.getPublicCertificatePath(), new String(zkClient.readData(payPRXI.getPublicCertificatePath())));
            }

            if(StringUtils.isNotBlank(payPRXI.getPrivateCertificatePath()))
            {
                tempMap.put(payPRXI.getPrivateCertificatePath(), new String(zkClient.readData(payPRXI.getPrivateCertificatePath())));
            }
        }

        map = ImmutableMap.copyOf(tempMap);
    }

    public static String getCertificateByZkPath(String zkPath)
    {
        return map.get(zkPath);
    }

}
