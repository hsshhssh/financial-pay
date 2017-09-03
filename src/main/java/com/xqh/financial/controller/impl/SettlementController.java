package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.xqh.financial.controller.api.ISettlementController;
import com.xqh.financial.entity.PayAppSettlement;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayUserSettlement;
import com.xqh.financial.entity.vo.PayAppSettlementVO;
import com.xqh.financial.entity.vo.PayUserSettlementVO;
import com.xqh.financial.mapper.PayAppSettlementMapper;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.mapper.PayUserSettlementMapper;
import com.xqh.financial.utils.*;
import com.xqh.financial.utils.jobs.SettlementJobs;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/5/14.
 */
@RestController
public class SettlementController implements ISettlementController
{
    @Autowired
    private SettlementJobs jobs;

    @Autowired
    private PayAppSettlementMapper appSettlementMapper;

    @Autowired
    private PayUserSettlementMapper userSettlementMapper;

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Override
    public Map<Integer, PayUserSettlement> getUserSettlementByDay(@PathVariable("day") int day)
    {

        List<PayOrder> orderList = jobs.getOrderListByDay(CommonUtils.getZeroHourTime(day), CommonUtils.getZeroHourTime(day+1));

        return jobs.getUserSettlement(orderList);

    }

    @Override
    public Map<Integer, PayAppSettlementVO> getAppSettlementByDay(@PathVariable("day") int day)
    {
        List<PayOrder> orderList = jobs.getOrderListByDay(CommonUtils.getZeroHourTime(day), CommonUtils.getZeroHourTime(day+1));

        Map<Integer, PayAppSettlementVO> res = Maps.newHashMap();

        Map<Integer, PayAppSettlement> map = jobs.getAppSettlement(orderList);

        for (Integer i : map.keySet())
        {
            res.put(i, DozerUtils.map(map.get(i), PayAppSettlementVO.class));
        }

        return res;

    }

    @Override
    public PageResult<PayAppSettlementVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                               @RequestParam(value = "page", defaultValue = "1") int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @RequestParam(value = "sort", required = false) Sort sort)
    {

        if(sort == null || sort.size() == 0) {
            sort = new Sort(Arrays.asList("id_desc"));
        }

        Example example = new ExampleBuilder(PayAppSettlement.class).search(search).sort(sort).build();

        Page<PayAppSettlement> settlementList = (Page<PayAppSettlement>) appSettlementMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(settlementList.getTotal(), DozerUtils.mapList(settlementList.getResult(), PayAppSettlementVO.class));

    }

    @Override
    public PageResult<PayUserSettlementVO> userList(@RequestParam("search") @Valid @NotNull Search search,
                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                    @RequestParam(value = "sort", required = false) Sort sort) {
        if(sort == null || sort.size() == 0)
        {
            sort = new Sort(Arrays.asList("id_desc"));
        }

        Example example = new ExampleBuilder(PayUserSettlement.class).search(search).sort(sort).build();

        Page<PayUserSettlement> settlementList = (Page<PayUserSettlement>) userSettlementMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(settlementList.getTotal(), DozerUtils.mapList(settlementList.getResult(), PayUserSettlementVO.class));
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

    @Override
    public List<PayAppSettlementVO> appMonthList(@RequestParam(value = "userId", required = false) Integer userId,
                                                 @RequestParam(value = "appId", required = false) Integer appId,
                                                 @RequestParam(value = "month", required = false) Integer month,
                                                 @RequestParam(value = "year", required = false) Integer year)
    {

        Search search = new Search();
        if(userId != null)
        {
            search.put("userId_eq", userId);
        }
        if(appId != null)
        {
            search.put("appId_eq", appId);
        }


        // 结算时间段 以月为单位
        List<Integer> monthStartEndTime;
        if(month == null)
        {
            // 取当前月
            Calendar cal = Calendar.getInstance();
            monthStartEndTime = CommonUtils.getMonthStartEndTime(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        }
        else
        {
            monthStartEndTime = CommonUtils.getMonthStartEndTime(month, year);
        }
        search.put("orderTime_gte", monthStartEndTime.get(0));
        search.put("orderTime_lt", monthStartEndTime.get(1));

        Example example = new ExampleBuilder(PayAppSettlement.class).search(search).build();

        List<PayAppSettlement> list = appSettlementMapper.selectByExample(example);

        // 汇总数据
        int nowTime = (int) (System.currentTimeMillis()/1000);
        Multimap<Integer, PayAppSettlement> appSettlementMap = ArrayListMultimap.create();
        for (PayAppSettlement payAppSettlement : list)
        {
            appSettlementMap.put(payAppSettlement.getAppId(), payAppSettlement);
        }

        List<PayAppSettlement> resList = Lists.newArrayList();
        for (Integer _appId : appSettlementMap.keySet())
        {
            double totalMoney = 0;
            double totalHandlingCharge = 0;
            double settlementMoney = 0;
            int _userId = 0;
            for (PayAppSettlement settlement : appSettlementMap.get(_appId))
            {
                _userId = settlement.getUserId();
                totalMoney = DoubleUtils.add(totalMoney, settlement.getTotalMoney());
                totalHandlingCharge = DoubleUtils.add(totalHandlingCharge, settlement.getTotalHandlingCharge());
                settlementMoney = DoubleUtils.add(settlementMoney, settlement.getSettlementMoney());
            }

            PayAppSettlement payAppSettlement = new PayAppSettlement();
            payAppSettlement.setUserId(_userId);
            payAppSettlement.setAppId(_appId);
            payAppSettlement.setTotalMoney(totalMoney);
            payAppSettlement.setTotalHandlingCharge(totalHandlingCharge);
            payAppSettlement.setSettlementMoney(settlementMoney);
            payAppSettlement.setOrderTime(monthStartEndTime.get(0));
            payAppSettlement.setUpdateTime(nowTime);
            payAppSettlement.setCreateTime(nowTime);

            resList.add(payAppSettlement);
        }

        return DozerUtils.mapList(resList, PayAppSettlementVO.class);
    }
}
