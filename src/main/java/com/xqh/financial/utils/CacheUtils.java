package com.xqh.financial.utils;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.PayUser;
import com.xqh.financial.mapper.PayAppMapper;
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

    private static PayAppMapper payAppMapper = SpringUtils.getBean(PayAppMapper.class);

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

    private static LoadingCache<Integer, String> payAppIdToName = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<Integer, String>()
            {
                @Override
                public String load(Integer key) throws Exception
                {
                    PayApp payApp = payAppMapper.selectByPrimaryKey(key);

                    return null != payApp ? payApp.getAppName() : "";
                }
            });

    /**
     * 通过userId取得userName
     * @param id
     * @return
     */
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

    /**
     * 通过appId取得appName
     * @param id
     * @return
     */
    public static String getAppNameById(int id)
    {
        String name = null;

        try
        {
            name = payAppIdToName.get(id);
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        if(StringUtils.isBlank(name))
        {
            payAppIdToName.refresh(id);
        }

        return name;
    }


}
