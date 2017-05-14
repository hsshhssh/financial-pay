package com.xqh.financial.service;

import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hssh on 2017/5/9.
 */
@Service
public class PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;


    /**
     * 根据订单流水号查询订单
     * @param orderSerial
     * @return
     */
    public PayOrder selectByOrderSerial(int orderSerial)
    {
        Search search = new Search();
        search.put("orderSerial_eq", orderSerial);
        List<PayOrder> list = payOrderMapper.selectByExample(new ExampleBuilder(PayOrder.class).search(search).build());

        return list.size() > 0 ? list.get(0) : null;
    }
}
