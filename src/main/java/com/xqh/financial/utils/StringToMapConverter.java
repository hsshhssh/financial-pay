package com.xqh.financial.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by hssh on 2017/5/14.
 */
@Component
public class StringToMapConverter implements Converter<String, Search>
{
    @Override
    public Search convert(String s)
    {
        JSONObject jsonObject = JSONObject.parseObject(s);
        return new Search(jsonObject);
    }
}
