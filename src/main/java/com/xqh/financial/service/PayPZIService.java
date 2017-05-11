package com.xqh.financial.service;

import com.xqh.financial.entity.PayPZI;
import com.xqh.financial.mapper.PayPZIMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by hssh on 2017/5/9.
 */
@Service
public class PayPZIService {

    @Autowired
    private PayPZIMapper payPZIMapper;

    public PayPZI select(int userId, int appId) {
        Search search = new Search();
        search.put("userId_eq", userId);
        search.put("appId_eq", appId);
        Example example = new ExampleBuilder(PayPZI.class).search(search).build();

        List<PayPZI> list = payPZIMapper.selectByExample(example);
        return list.size() > 0 ? list.get(0) : null;
    }


}
