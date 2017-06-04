package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.xqh.financial.controller.api.IPayAppController;
import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.dto.PayAppCreateDTO;
import com.xqh.financial.entity.dto.PayAppUpdateDTO;
import com.xqh.financial.entity.vo.PayAppVO;
import com.xqh.financial.mapper.PayAppMapper;
import com.xqh.financial.utils.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    @Override
    public PageResult<PayAppVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size)
    {
        Example example = new ExampleBuilder(PayApp.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayApp> payAppList = (Page<PayApp>) payAppMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(payAppList.getTotal(), DozerUtils.mapList(payAppList.getResult(), PayAppVO.class));

    }

    @Override
    public int create(@RequestBody @Valid @NotNull PayAppCreateDTO dto)
    {
        PayApp app = DozerUtils.map(dto, PayApp.class);

        int nowTime = (int) (System.currentTimeMillis()/1000);

        app.setSecretkey(CommonUtils.getMd5(UUID.randomUUID().toString()).toUpperCase());
        app.setUpdateTime(nowTime);
        app.setCreateTime(nowTime);

        return payAppMapper.insertSelective(app);

    }

    @Override
    public int update(@RequestBody @Valid @NotNull PayAppUpdateDTO dto)
    {
        PayApp app = DozerUtils.map(dto, PayApp.class);

        int nowTime = (int) (System.currentTimeMillis()/1000);

        Search search = new Search();
        search.put("id_eq", app.getId());
        search.put("userId_eq", app.getUserId());

        Example example = new ExampleBuilder(PayApp.class).search(search).build();

        List<PayApp> payAppList = payAppMapper.selectByExample(example);

        if(payAppList.size() != 1)
        {
            return 0;
        }

        app.setUpdateTime(nowTime);
        return payAppMapper.updateByPrimaryKeySelective(app);
    }
}
