package com.xqh.financial.entity.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/10/3.
 */
@Data
public class SwitchPlatformDTO
{
    @NotNull @Min(1)
    private Integer payType;

    @NotNull @Min(1)
    private Integer platformId;

    @NotNull @Min(1)
    private Integer userId;

}
