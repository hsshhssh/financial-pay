package com.xqh.financial.utils.converter;

import com.xqh.financial.utils.CacheUtils;
import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/5/27.
 */
public class DozerPayUserConverter implements CustomConverter
{
    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        return CacheUtils.getUserNameById((Integer) sourceFieldValue);
    }
}
