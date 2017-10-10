package com.xqh.financial.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/10/3.
 */
@Data
public class PayVerifyUpdateDTO
{
    @NotNull @Min(1)
    private Integer id;
    @NotNull
    private Double transfer1;
    @NotNull
    private Double transfer2;
    @NotNull
    private Double transfer3;
    @NotNull
    private Double transfer4;
    @NotNull
    private Double transfer5;
    @NotNull
    private Double transfer6;
    @NotBlank
    private String remark;

}
