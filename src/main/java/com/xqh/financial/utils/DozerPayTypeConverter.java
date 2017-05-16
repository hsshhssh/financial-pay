package com.xqh.financial.utils;

import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/5/16.
 */
public class DozerPayTypeConverter implements CustomConverter
{
    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        int payType = (int) sourceFieldValue;
        if(payType == Constant.WXWAP_PAY_TYPE)
        {
            return "微信wap支付";
        }
        else
        {
            return null;
        }
    }
}
