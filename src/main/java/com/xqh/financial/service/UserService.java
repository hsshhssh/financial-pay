package com.xqh.financial.service;

import com.xqh.financial.entity.PayUser;
import com.xqh.financial.mapper.PayUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hssh on 2017/5/4.
 */
@Service
public class UserService {

    @Autowired
    PayUserMapper payUserMapper;

    public int insertSingle(PayUser payUser) {
        int nowTime = (int) (System.currentTimeMillis()/1000);
        payUser.setCreateTime(nowTime);
        payUser.setUpdateTime(nowTime);
        payUserMapper.insertSelective(payUser);
        return payUser.getId();
    }

}
