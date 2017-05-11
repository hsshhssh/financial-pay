package com.xqh.financial.service;

import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.mapper.PayOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hssh on 2017/5/9.
 */
@Service
public class PayOrderService {

    @Autowired
    private PayOrderMapper payOrderMapper;

}
