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

    @Length(min = 6, max = 50)
    private String password;

    @Length(min = 6, max = 50)
    private String name;

    @Length(max = 20)
    private String phone;

}
