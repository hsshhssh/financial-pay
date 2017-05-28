package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IPayAppController;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hssh on 2017/5/27.
 */
@RestController
public class PayAppController implements IPayAppController
{
    @Autowired
    private PayAppMapper payAppMapper;


    @Override
    public List<PayApp> listNoPage(@RequestParam("userId") int userId)
    {
        Search search = new Search();
        search.put("userId_eq", userId);

        Example example = new ExampleBuilder(PayApp.class).search(search).sort(Arrays.asList("id_desc")).build();

        List<PayApp> list = payAppMapper.selectByExample(example);

        return list;

    }
}
