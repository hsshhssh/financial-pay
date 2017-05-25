package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.xqh.financial.controller.api.IPayCFRController;
import com.xqh.financial.entity.PayCFR;
import com.xqh.financial.mapper.PayCFRMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * Created by hssh on 2017/5/25.
 */
public class PayCFRController implements IPayCFRController
{
    @Autowired
    private PayCFRMapper payCFRMapper;


    @Override
    public PageResult<PayCFR> list(@RequestParam("search") @Valid @NotNull Search search,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size)
    {

        Example example = new ExampleBuilder(PayCFR.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayCFR> payCFRList = (Page<PayCFR>) payCFRMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(payCFRList.getTotal(), payCFRList.getResult());


    }

}
