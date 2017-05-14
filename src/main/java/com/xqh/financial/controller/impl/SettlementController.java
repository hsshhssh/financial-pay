package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.ISettlementController;
import com.xqh.financial.entity.PayAppSettlement;
import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayUserSettlement;
import com.xqh.financial.utils.Jobs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
