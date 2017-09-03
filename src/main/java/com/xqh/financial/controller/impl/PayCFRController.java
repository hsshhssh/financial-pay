package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.xqh.financial.controller.api.IPayCFRController;
import com.xqh.financial.entity.PayCFR;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.entity.vo.PayCFRVO;
import com.xqh.financial.mapper.PayCFRMapper;
import com.xqh.financial.service.XQHPayService;
import com.xqh.financial.service.ZPayService;
import com.xqh.financial.utils.*;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * Created by hssh on 2017/5/25.
 */
@RestController
public class PayCFRController implements IPayCFRController
{
    @Autowired
    private PayCFRMapper payCFRMapper;

    @Autowired
    private XQHPayService xqhPayService;

    private static Logger logger = LoggerFactory.getLogger(PayCFRController.class);


    @Override
    public PageResult<PayCFRVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size)
    {

        Example example = new ExampleBuilder(PayCFR.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayCFR> payCFRList = (Page<PayCFR>) payCFRMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(payCFRList.getTotal(), DozerUtils.mapList(payCFRList.getResult(), PayCFRVO.class));


    }

    @Override
    public int callback(@RequestParam("id") @Min(1) int id)
    {
        PayCFR payCFR = payCFRMapper.selectByPrimaryKey(id);
        if(null == payCFR)
        {
            logger.warn("回调失败记录再次回调 id不合法 id:{}", id);
            return 0;
        }

        if(Constant.CALLBACK_SUCCESS == payCFR.getState())
        {
            return 1;
        }

            HttpResult httpResult = HttpUtils.get(payCFR.getCallbackUrl());

            xqhPayService.updateOrderStatus(httpResult, payCFR.getOrderId(), payCFR.getId());

        return 1;
    }

}
