package com.xqh.financial.utils.converter;

import com.xqh.financial.utils.PayPlatformEnum;
import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/9/16.
 */
public class DozerPlatformNameConverter implements CustomConverter
{
    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        for (PayPlatformEnum platformEnum : PayPlatformEnum.values())
        {
            if(platformEnum.getCode().equals((String) sourceFieldValue))
            {
                return platformEnum.getName();
            }
        }

        return null;
    }
}
