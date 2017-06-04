package com.xqh.financial.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/6/4.
 */
@Data
public class PayAppUpdateDTO
{
    @NotNull
    @Min(1)
    private Integer id;

    @NotNull
    @Min(1)
    private Integer userId;

    @NotNull
    @Length(max = 50)
    private String appName;

    @NotNull
    @Length(max = 500)
    private String callbackUrl;

    @NotNull
    @Length(max = 500)
    private String nodifyUrl;

}
