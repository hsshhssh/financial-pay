package com.xqh.financial.mapper;

import com.xqh.financial.entity.PayAppPlatform;
import com.xqh.financial.entity.dto.SwitchPlatformDTO;
import tk.mybatis.mapper.common.Mapper;

public interface PayAppPlatformMapper extends Mapper<PayAppPlatform>
{

    public int validPlatform(SwitchPlatformDTO dto);

    public int invalidPlatform(SwitchPlatformDTO dto);

}