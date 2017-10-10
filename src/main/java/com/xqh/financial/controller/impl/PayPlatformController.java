package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.xqh.financial.controller.api.IPayPlatformController;
import com.xqh.financial.entity.PayPlatform;
import com.xqh.financial.entity.vo.PayPlatformVO;
import com.xqh.financial.mapper.PayPlatformMapper;
import com.xqh.financial.utils.DozerUtils;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * Created by hssh on 2017/10/8.
 */
@RestController
public class PayPlatformController implements IPayPlatformController
{
    private static Logger logger = LoggerFactory.getLogger(PayPlatformController.class);

    @Autowired
    private PayPlatformMapper payPlatformMapper;

    @Override
    public PageResult<PayPlatformVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                          @RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size)
    {
        Example example = new ExampleBuilder(PayPlatform.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayPlatform> payPlatformList = (Page<PayPlatform>) payPlatformMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(payPlatformList.getTotal(), DozerUtils.mapList(payPlatformList, PayPlatformVO.class));
    }
}
