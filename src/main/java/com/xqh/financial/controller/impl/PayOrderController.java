package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.xqh.financial.controller.api.IPayOrderController;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.vo.PayOrderVO;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.utils.DozerUtils;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;

/**
 * Created by hssh on 2017/5/14.
 */
@RestController
public class PayOrderController implements IPayOrderController
{
    @Autowired
    private PayOrderMapper payOrderMapper;

    @Override
    public PageResult<PayOrderVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                       @RequestParam(value = "page", defaultValue = "1")  int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size)
    {
        Example example = new ExampleBuilder(PayOrder.class).search(search).sort(Collections.singletonList("id_desc")).build();

        Page<PayOrder> orderPage = (Page<PayOrder>) payOrderMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(orderPage.getTotal(), DozerUtils.mapList(orderPage.getResult(), PayOrderVO.class));

    }

    @Override
    public PayOrder queryOne(@PathVariable("id") int id)
    {
        return null;
    }

    @Override
    public PayOrder test() {
        return payOrderMapper.selectByPrimaryKey(6);
    }
}
