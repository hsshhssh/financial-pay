package com.xqh.financial.utils.converter;

import com.xqh.financial.utils.CacheUtils;
import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/5/31.
 */
public class DozerPayAppConverter implements CustomConverter
{
    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        return CacheUtils.getAppNameById((Integer) sourceFieldValue);
    }
}
