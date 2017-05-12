package com.xqh.financial.service;

import com.xqh.financial.entity.PayApp;
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




}
