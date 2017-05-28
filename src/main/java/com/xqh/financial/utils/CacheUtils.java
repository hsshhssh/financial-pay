package com.xqh.financial.utils;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xqh.financial.entity.PayUser;
import com.xqh.financial.mapper.PayUserMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by hssh on 2017/5/27.
 */
public class CacheUtils
{

    private static PayUserMapper payUserMapper = SpringUtils.getBean(PayUserMapper.class);

    private static LoadingCache<Integer, String> payUserIdToName = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<Integer, String>()
            {
                @Override
                public String load(Integer key) throws Exception
                {
                    PayUser payUser = payUserMapper.selectByPrimaryKey(key);
                    if(payUser != null)
                    {
                        return payUser.getName();
                    }
                    else
                    {
                        return "";
                    }
                }
            });

    public static String getUserNameById(int id)
    {
        String name = null;
        try
        {
            name = payUserIdToName.get(id);
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        if(StringUtils.isBlank(name))
        {
            payUserIdToName.refresh(id);
        }

        return name;
    }

}
