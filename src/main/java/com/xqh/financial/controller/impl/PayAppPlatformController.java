package com.xqh.financial.controller.impl;

import com.google.common.base.Throwables;
import com.xqh.financial.controller.api.IPayAppPlatformController;
import com.xqh.financial.entity.dto.SwitchPlatformDTO;
import com.xqh.financial.service.PayAppPlatformService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ErrorResponseEunm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/10/3.
 */
@RestController
public class PayAppPlatformController implements IPayAppPlatformController
{
    private static Logger logger = LoggerFactory.getLogger(PayAppPlatformController.class);

    @Autowired
    private PayAppPlatformService payAppPlatformService;

    @Override
    public boolean switchPlatform(@RequestBody @Valid @NotNull SwitchPlatformDTO dto,
                                  HttpServletResponse resp)
    {
        // 检验参数
        try
        {
            payAppPlatformService.validDTO(dto);
        } catch (Exception e)
        {
            logger.error("切换支付通道 校验异常 dto:{} e:{}", dto, Throwables.getStackTraceAsString(e));
            CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_METHOD_ARGS);
            return false;
        }

        payAppPlatformService.switchPlatform(dto);
        return true;
    }
}
