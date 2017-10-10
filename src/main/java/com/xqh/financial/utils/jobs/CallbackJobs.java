package com.xqh.financial.utils.jobs;

import com.xqh.financial.entity.PayCFR;
import com.xqh.financial.entity.other.HttpResult;
import com.xqh.financial.mapper.PayCFRMapper;
import com.xqh.financial.service.XQHPayService;
import com.xqh.financial.utils.Constant;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.HttpUtils;
import com.xqh.financial.utils.Search;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hssh on 2017/9/3.
 */
@Component
public class CallbackJobs
{
    public static Logger logger = LoggerFactory.getLogger(CallbackJobs.class);

    // 最大回调次数
    public static final int MAX_TIMES = 5;

    @Autowired
    private PayCFRMapper payCFRMapper;

    @Autowired
    private XQHPayService xqhPayService;

    @Scheduled(cron = "0 0/5 * * * ? ")
    public void callback()
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);

        logger.info("自动回调开始 nowTime:{}", nowTime);

        List<PayCFR> payCFRList = getCallbackRecord();

        logger.info("回调记录数 size:{}", payCFRList.size());

        for (PayCFR payCFR : payCFRList)
        {
            HttpResult httpResult = HttpUtils.get(payCFR.getCallbackUrl());
            xqhPayService.updateOrderStatus(httpResult, payCFR.getOrderId(), payCFR.getId());

            logger.info("自动回调 payCFR:{} 回调结果:{}", payCFR, httpResult);
        }

    }


    public List<PayCFR> getCallbackRecord()
    {
        Search search = new Search();
        search.put("state_eq", Constant.FAIL_STATE);
        search.put("callbackTimes_lt", MAX_TIMES);

        Example example = new ExampleBuilder(PayCFR.class).search(search).sort(Arrays.asList("id_desc")).build();


        List<PayCFR> payCFRs = payCFRMapper.selectByExampleAndRowBounds(example, new RowBounds(1, 500));

        return payCFRs;

    }

}