package com.xqh.financial.utils.converter;

import com.xqh.financial.utils.DoubleUtils;
import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/5/28.
 */
public class DozerDiv100Converter implements CustomConverter
{
    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass)
    {
        if(sourceFieldValue instanceof  Integer)
        {
            return DoubleUtils.div(Double.valueOf((Integer)sourceFieldValue), 100);
        }
        else if(sourceFieldValue instanceof Double)
        {
            return DoubleUtils.div((Double) sourceFieldValue, 100);
        }
        return null;
    }
}
