package com.xqh.financial.utils;

import lombok.Data;

import java.util.List;

/**
 * Created by hssh on 2017/5/14.
 */
@Data
public class PageResult<T>
{
    private List<T> list;

    private long total;

    public PageResult()
    {
    }

    public PageResult(long total, List<T> list)
    {
        this.list = list;
        this.total = total;
    }
}
