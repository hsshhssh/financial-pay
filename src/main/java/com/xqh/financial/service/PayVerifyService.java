package com.xqh.financial.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.xqh.financial.entity.PayVerify;
import com.xqh.financial.mapper.PayVerifyMapper;
import com.xqh.financial.utils.DoubleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/11/28.
 */
@Service
public class PayVerifyService
{
    @Autowired
    private PayVerifyMapper payVerifyMapper;

    public Map<Integer, Double> getDiffMap()
    {

        Map<Integer, Double> resultMap = Maps.newHashMap();

        List<PayVerify> payVerifyList = payVerifyMapper.selectAll();

        Multimap<Integer, PayVerify> userIdPayVerifyMap = ArrayListMultimap.create();

        for (PayVerify payVerify : payVerifyList)
        {
            userIdPayVerifyMap.put(payVerify.getUserId(), payVerify);
        }

        for (Integer userId : userIdPayVerifyMap.keySet())
        {
            double tempAmount = 0;
            for (PayVerify payVerify : userIdPayVerifyMap.get(userId))
            {

                tempAmount = DoubleUtils.add(tempAmount, DoubleUtils.div(payVerify.getDiff(), 100));
            }
            resultMap.put(userId, tempAmount);
        }

        return resultMap;
    }

}
