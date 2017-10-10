package com.xqh.financial.service;

import com.xqh.financial.entity.PayPlatform;
import com.xqh.financial.entity.PayUser;
import com.xqh.financial.entity.dto.SwitchPlatformDTO;
import com.xqh.financial.mapper.PayAppPlatformMapper;
import com.xqh.financial.mapper.PayPlatformMapper;
import com.xqh.financial.mapper.PayUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

/**
 * Created by hssh on 2017/10/3.
 */
@Service
public class PayAppPlatformService
{
    private static Logger logger = LoggerFactory.getLogger(PayAppPlatformService.class);

    @Autowired
    private PayPlatformMapper payPlatformMapper;

    @Autowired
    private PayUserMapper payUserMapper;

    @Autowired
    private PayAppPlatformMapper payAppPlatformMapper;

    public void validDTO(SwitchPlatformDTO dto)
    {
        PayPlatform payPlatform = payPlatformMapper.selectByPrimaryKey(dto.getPlatformId());

        if(null == payPlatform || !payPlatform.getPayType().equals(dto.getPayType()))
        {
            throw new ValidationException("支付平台信息异常");
        }

        PayUser payUser = payUserMapper.selectByPrimaryKey(dto.getUserId());

        if(null == payUser)
        {
            throw new ValidationException("支付用户信息");
        }
    }

    @Transactional
    public void switchPlatform(SwitchPlatformDTO dto)
    {
        payAppPlatformMapper.validPlatform(dto);

        payAppPlatformMapper.invalidPlatform(dto);
    }

}
