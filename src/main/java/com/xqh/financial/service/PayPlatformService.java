package com.xqh.financial.service;

import com.xqh.financial.entity.PayPlatform;
import com.xqh.financial.mapper.PayPlatformMapper;
import com.xqh.financial.utils.Constant;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by hssh on 2017/5/11.
 */
@Service
public class PayPlatformService {

    @Autowired
    private PayPlatformMapper payPlatformMapper;


    /**
     * 注意当存在多个有效的支付平台是 返回为null
     * @param appId
     * @param payType
     * @return
     */
    public PayPlatform selectValidRecordByAppIdPayType(int appId, int payType)
    {
        Search search = new Search();
        search.put("appId_eq", appId);
        search.put("payType_eq", payType);
        search.put("state_eq", Constant.ENABLE_STATE);

        Example example = new ExampleBuilder(PayPlatform.class).search(search).build();

        List<PayPlatform> list = payPlatformMapper.selectByExample(example);

        if(list.size() == 1)
        {
            return list.get(0);
        }
        else
        {
            return null;
        }
    }

}
