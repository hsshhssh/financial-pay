package com.xqh.financial.utils.converter;

import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/6/12.
 */
public class DozerUserRoleConverter implements CustomConverter
{

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        int role = (int) sourceFieldValue;
        if(1 == role)
        {
            return "商户";
        }
        else
        {
            return "";
        }
    }
}
