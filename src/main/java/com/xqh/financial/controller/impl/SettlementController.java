package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.xqh.financial.controller.api.ISettlementController;
import com.xqh.financial.entity.PayAppSettlement;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayUserSettlement;
import com.xqh.financial.entity.vo.PayAppSettlementVO;
import com.xqh.financial.mapper.PayAppSettlementMapper;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.mapper.PayUserSettlementMapper;
import com.xqh.financial.utils.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/5/14.
 */
@RestController
public class SettlementController implements ISettlementController
{
    @Autowired
    private Jobs jobs;

    @Autowired
    private PayAppSettlementMapper appSettlementMapper;

    @Autowired
    private PayUserSettlementMapper userSettlementMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Override
    public Map<Integer, PayUserSettlement> getUserSettlementByDay(@PathVariable("day") int day)
    {

        List<PayOrder> orderList = jobs.getOrderListByDay(day);

        return jobs.getUserSettlement(orderList);

    }

    @Override
    public Map<Integer, PayAppSettlement> getAppSettlementByDay(@PathVariable("day") int day)
    {
        List<PayOrder> orderList = jobs.getOrderListByDay(day);

        return jobs.getAppSettlement(orderList);

    }

    @Override
    public PageResult<PayAppSettlementVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size)
    {

        Example example = new ExampleBuilder(PayAppSettlement.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayAppSettlement> settlementList = (Page<PayAppSettlement>) appSettlementMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(settlementList.getTotal(), DozerUtils.mapList(settlementList.getResult(), PayAppSettlementVO.class));

    }

    @Override
    public List<Double> getPayData(@RequestParam("userId") @Min(1) Integer userId)
    {
        List<Double> res = Lists.newArrayList();

        // 取得昨日结算金额 凌晨一点=》昨天订单结算
        Search searchSet = new Search();
        searchSet.put("createTime_gt", CommonUtils.getZeroHourTime(0));
        searchSet.put("createTime_lt", CommonUtils.getZeroHourTime(1));
        searchSet.put("userId_eq", userId);

        Example exampleSet = new ExampleBuilder(PayUserSettlement.class).search(searchSet).sort(Arrays.asList("id_desc")).build();

        List<PayUserSettlement> setList = userSettlementMapper.selectByExample(exampleSet);

        res.add(setList.size()>0 ? setList.get(0).getTotalMoney() : 0);


        // 取得昨日订单数
        Search searchYesOrder = new Search();
        searchYesOrder.put("createTime_gt", CommonUtils.getZeroHourTime(-1));
        searchYesOrder.put("createTime_lt", CommonUtils.getZeroHourTime(0));
        searchYesOrder.put("userId_eq", userId);

        Example exampleYesOrder = new ExampleBuilder(PayOrder.class).search(searchYesOrder).build();

        int yesOrderCount = payOrderMapper.selectCountByExample(exampleYesOrder);
        res.add((double) yesOrderCount);


        // 取得今日订单数
        Search searchTodayOrder = new Search();
        searchTodayOrder.put("createTime_gt", CommonUtils.getZeroHourTime(0));
        searchTodayOrder.put("createTime_lt", CommonUtils.getZeroHourTime(1));
        searchTodayOrder.put("userId_eq", userId);

        Example exampleTodayOrder = new ExampleBuilder(PayOrder.class).search(searchTodayOrder).build();

        int todayOrderCount = payOrderMapper.selectCountByExample(exampleTodayOrder);
        res.add((double) todayOrderCount);

        return res;
    }
}
