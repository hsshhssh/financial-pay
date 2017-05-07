package com.xqh.financial.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;

/**
 * Created by hssh on 2017/5/4.
 */
@Data
public class PayUserCreateDTO {

    @Length(min = 6, max = 50)
    private String username;

    @Length(min = 6, max = 50)
    private String password;

    @Length(min = 6, max = 50)
    private String name;

    @Length(max = 20)
    private String phone;

    @Min(1)
    private Integer role;

}
