package com.xqh.financial.utils;

import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/5/16.
 */
public class DozerCallbackStateConverter implements CustomConverter {
    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        int callbackState = (int) sourceFieldValue;
        if(Constant.CALLBACK_SUCCESS == callbackState)
        {
            return "成功";
        }
        else if(Constant.CALLBACK_FAIL == callbackState)
        {
            return "失败";
        }
        else
        {
            return null;
        }
    }
}
