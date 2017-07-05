package com.xqh.financial.service;

import com.xqh.financial.entity.PayAppPlatform;
import com.xqh.financial.mapper.PayAppPlatformMapper;
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
public class AppPlatformService {

    @Autowired
    private PayAppPlatformMapper payAppPlatformMapper;

    public PayAppPlatform selectByAppIdPlatformId(int appId, int platformId)
    {
        Search search = new Search();
        search.put("appId_eq", appId);
        search.put("platformId_eq", platformId);

        Example example = new ExampleBuilder(PayAppPlatform.class).search(search).build();

        List<PayAppPlatform> list = payAppPlatformMapper.selectByExample(example);

        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 注意当存在多个有效的支付平台是 返回为null
     * @param appId
     * @param payType
     * @return
     */
    public PayAppPlatform selectValidRecordByAppIdPayType(int appId, int payType)
    {
        Search search = new Search();
        search.put("appId_eq", appId);
        search.put("payType_eq", payType);
        search.put("state_eq", Constant.ENABLE_STATE);

        Example example = new ExampleBuilder(PayAppPlatform.class).search(search).build();

        List<PayAppPlatform> list = payAppPlatformMapper.selectByExample(example);

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
