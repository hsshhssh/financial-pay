package com.xqh.financial.utils;

import org.dozer.CustomConverter;

/**
 * Created by hssh on 2017/5/4.
 */
public class DozerPasswordEncryptConverter implements CustomConverter {

    /**
     * 明文->密文
     */
    @Override
    public Object convert(Object dest, Object source, Class<?> destinationClass, Class<?> sourceClass) {
        if(null != source) {
            return CommonUtils.getMd5((String) source);
        }

        return null;

    }
}
