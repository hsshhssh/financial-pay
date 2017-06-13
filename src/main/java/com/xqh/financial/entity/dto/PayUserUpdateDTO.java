package com.xqh.financial.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

/**
 * Created by hssh on 2017/5/5.
 */
@Data
public class PayUserUpdateDTO {

    @Min(1)
    private Integer id;

    @Length( max = 50)
    private String name;

    @Length(max = 20)
    private String phone;

}
