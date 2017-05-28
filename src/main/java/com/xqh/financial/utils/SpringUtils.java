package com.xqh.financial.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by hssh on 2017/5/27.
 */
@Component
public class SpringUtils implements ApplicationContextAware
{
    private static ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ac = applicationContext;
    }

    public static <T> T getBean(Class<T> cls)
    {
        return ac.getBean(cls);
    }
}
