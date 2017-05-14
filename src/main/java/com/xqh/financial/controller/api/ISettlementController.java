package com.xqh.financial.controller.api;

import com.xqh.financial.entity.PayAppSettlement;
import com.xqh.financial.entity.PayUserSettlement;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by hssh on 2017/5/14.
 */
@RequestMapping("/xqh/financial/settlement")
public interface ISettlementController
{

    @RequestMapping("/user/{day}")
    public Map<Integer, PayUserSettlement> getUserSettlementByDay(@PathVariable("day") int day);


    @RequestMapping("/app/{day}")
    public Map<Integer, PayAppSettlement> getAppSettlementByDay(@PathVariable("day") int day);

}
