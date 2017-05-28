package com.xqh.financial.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.xqh.financial.entity.PayAppSettlement;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayUserSettlement;
import com.xqh.financial.mapper.PayAppSettlementMapper;
import com.xqh.financial.mapper.PayOrderMapper;
import com.xqh.financial.mapper.PayUserSettlementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/5/13.
 */
@Component
public class Jobs
{
    private static Logger logger = LoggerFactory.getLogger(Jobs.class);

    @Autowired
    PayOrderMapper payOrderMapper;

    @Autowired
    PayAppSettlementMapper appSettlementMapper;

    @Autowired
    PayUserSettlementMapper userSettlementMapper;

    @Scheduled(cron = "0 0 1 * * ? ")
    public void settlement()
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);
        logger.info("结算开始 当前时间: {}", nowTime);

        // 取得昨天的订单
        List<PayOrder> orderList = getOrderListByDay(-1);
        logger.info("昨天订单数 size:{}", orderList.size());

        Map<Integer, PayAppSettlement> appSettlementMap = getAppSettlement(orderList);

        Map<Integer, PayUserSettlement> userSettlementMap = getUserSettlement(orderList);


        // 入库
        for (Integer appId : appSettlementMap.keySet())
        {
            appSettlementMapper.insertSelective(appSettlementMap.get(appId));
        }

        for(Integer userId : userSettlementMap.keySet())
        {
            userSettlementMapper.insertSelective(userSettlementMap.get(userId));
        }

    }


    /**
     * 取得订单列表
     * @param day 0今天  -1昨天 依次类推
     * @return
     */
    public List<PayOrder> getOrderListByDay(int day)
    {
        Search search = new Search();
        search.put("createTime_gt", CommonUtils.getZeroHourTime(day));
        search.put("createTime_lt", CommonUtils.getZeroHourTime(day + 1));

        Example example = new ExampleBuilder(PayOrder.class).search(search).build();

        List<PayOrder> orderList = payOrderMapper.selectByExample(example);

        return orderList;
    }


    /**
     * 应用结算
     * @param orderList
     * @return
     */
    public Map<Integer, PayAppSettlement> getAppSettlement(List<PayOrder> orderList)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        // appId=>payOrder
        Multimap<Integer, PayOrder> appOrderMap = ArrayListMultimap.create();
        // appId=>payAppSettlement
        Map<Integer, PayAppSettlement> appSettlementMap = Maps.newHashMap();

        for (PayOrder payOrder : orderList)
        {
            appOrderMap.put(payOrder.getAppId(), payOrder);
        }

        // 应用结算
        for (Integer appId : appOrderMap.keySet())
        {
            double totalMoney = 0;
            double totalHandlingCharge = 0;
            int userId = 0;
            for (PayOrder payOrder : appOrderMap.get(appId))
            {
                userId = payOrder.getUserId();
                totalMoney = DoubleUtils.add(totalMoney, payOrder.getMoney());
                double temp = DoubleUtils.mul(payOrder.getMoney(), DoubleUtils.div(payOrder.getInterestRate(), 10000)); // 利息单位万分之几
                totalHandlingCharge = DoubleUtils.add(totalHandlingCharge, temp);
            }

            PayAppSettlement payAppSettlement = new PayAppSettlement();
            payAppSettlement.setUserId(userId);
            payAppSettlement.setAppId(appId);
            payAppSettlement.setTotalMoney(totalMoney);
            payAppSettlement.setTotalHandlingCharge(totalHandlingCharge);
            payAppSettlement.setSettlementMoney(DoubleUtils.sub(totalMoney, totalHandlingCharge));
            payAppSettlement.setOrderTime(CommonUtils.getZeroHourTime(-1));
            payAppSettlement.setUpdateTime(nowTime);
            payAppSettlement.setCreateTime(nowTime);

            appSettlementMap.put(appId, payAppSettlement);

        }

        return appSettlementMap;
    }

    /**
     * 用户结算
     * @param orderList
     * @return
     */
    public Map<Integer, PayUserSettlement> getUserSettlement(List<PayOrder> orderList)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        // userId=>payOrder
        Multimap<Integer, PayOrder> userOrderMap = ArrayListMultimap.create();
        // appId=>payAppSettlement
        Map<Integer, PayUserSettlement> userSettlementMap = Maps.newHashMap();

        for (PayOrder payOrder : orderList)
        {
            userOrderMap.put(payOrder.getUserId(), payOrder);
        }


        // 用户结算
        for (Integer userId : userOrderMap.keySet())
        {
            double totalMoney = 0;
            double totalHandlingCharge = 0;

            for (PayOrder payOrder : userOrderMap.get(userId))
            {
                totalMoney = DoubleUtils.add(totalMoney, payOrder.getMoney());
                double temp = DoubleUtils.mul(payOrder.getMoney(), DoubleUtils.div(payOrder.getInterestRate(), 10000)); // 利息单位万分之几
                totalHandlingCharge = DoubleUtils.add(totalHandlingCharge, temp);
            }

            PayUserSettlement payUserSettlement = new PayUserSettlement();
            payUserSettlement.setUserId(userId);
            payUserSettlement.setTotalMoney(totalMoney);
            payUserSettlement.setTotalHandlingCharge(totalHandlingCharge);
            payUserSettlement.setSettlementMoney(DoubleUtils.sub(totalMoney, totalHandlingCharge));
            payUserSettlement.setOrderTime(CommonUtils.getZeroHourTime(-1));
            payUserSettlement.setCreateTime(nowTime);
            payUserSettlement.setUpdateTime(nowTime);

            userSettlementMap.put(userId, payUserSettlement);
        }

        return userSettlementMap;

    }

}
