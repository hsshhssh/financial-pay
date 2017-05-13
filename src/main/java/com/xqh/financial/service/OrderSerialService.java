package com.xqh.financial.service;

import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.exception.RepeatPayException;
import com.xqh.financial.mapper.PayOrderSerialMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import com.xqh.financial.utils.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by hssh on 2017/5/9.
 */
@Service
public class OrderSerialService {

    private static Logger logger = LoggerFactory.getLogger(OrderSerialService.class);

    @Autowired
    private PayOrderSerialMapper orderSerialMapper;

    @Autowired
    private PayOrderService payOrderService;


    /**
     *
     * @param payOrderSerial
     * @return
     */
    public int insert(PayOrderSerial payOrderSerial) throws RepeatPayException{

        int nowTime = (int) (System.currentTimeMillis()/1000);
        payOrderSerial.setCreateTime(nowTime);
        payOrderSerial.setUpdateTime(nowTime);

        try {
            orderSerialMapper.insertSelective(payOrderSerial);
        } catch (DuplicateKeyException e) {
            //e.printStackTrace();
            PayOrderSerial serial = selectOne(payOrderSerial.getAppId(), payOrderSerial.getUserOrderNo(), payOrderSerial.getRequestTime());
            logger.warn("支付重复请求 orderSerial:{} appId:{} request_time:{},user_order_no:{}", serial.getId(), serial.getAppId(), serial.getRequestTime(), serial.getUserOrderNo());

            // 判断订单是否已经支付
            PayOrder payOrder = payOrderService.selectByOrderSerial(serial.getId());
            if(payOrder != null)
            {
                throw new RepeatPayException(String.format("订单流水号 orderSerial:" + serial.getId() + " 重复支付"));
            }

            return serial.getId();
        }

        return payOrderSerial.getId();

    }

    public PayOrderSerial selectOne(int appId, String userOrderNo, int requestTime) {
        Search search = new Search();
        search.put("appId_eq", appId);
        search.put("userOrderNo_eq", userOrderNo);
        search.put("requestTime_eq", requestTime);
        Sort sort = new Sort();
        sort.add("id_desc");
        Example example = new ExampleBuilder(PayOrderSerial.class).search(search).sort(sort).build();
        List<PayOrderSerial> list = orderSerialMapper.selectByExample(example);
        return list.size() > 0 ? list.get(0) : null;
    }

}
