package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.xqh.financial.controller.api.IPayPlatformSettlementController;
import com.xqh.financial.entity.PayUPS;
import com.xqh.financial.entity.vo.PayPlatformSettlementVO;
import com.xqh.financial.entity.vo.PaySimplePlatformVO;
import com.xqh.financial.mapper.PayUPSMapper;
import com.xqh.financial.utils.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hssh on 2017/9/16.
 */
@RestController
public class PayPlatformSettlementController implements IPayPlatformSettlementController
{
    @Autowired
    private PayUPSMapper payUPSMapper;

    @Override
    public PageResult<PayPlatformSettlementVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                                    @RequestParam(value = "page", defaultValue = "1")  int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size)
    {
        Example example = new ExampleBuilder(PayUPS.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayUPS> payUPSPage = (Page<PayUPS>) payUPSMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult(payUPSPage.getTotal() , DozerUtils.mapList(payUPSPage.getResult(), PayPlatformSettlementVO.class));
    }

    @Override
    public List<PaySimplePlatformVO> simplePlatformList()
    {
        List<PaySimplePlatformVO> list = Lists.newArrayList();

        for (PayPlatformEnum platformEnum : PayPlatformEnum.values())
        {
            list.add(DozerUtils.map(platformEnum, PaySimplePlatformVO.class));
        }

        return list;

    }
}
