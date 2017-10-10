package com.xqh.financial.controller.api;

import com.xqh.financial.entity.dto.SwitchPlatformDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/10/3.
 */
@RequestMapping("/xqh/financial/pay/appPlatform")
public interface IPayAppPlatformController
{
    @PostMapping("switchPlatform")
    public boolean switchPlatform(@RequestBody @Valid @NotNull SwitchPlatformDTO dto,
                                  HttpServletResponse resp);

}
