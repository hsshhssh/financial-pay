package com.xqh.financial.entity.vo;

import lombok.Data;

/**
 * Created by hssh on 2017/5/5.
 */
@Data
public class PayUserVO {

    private Integer id;
    private String username;
    private String name;
    private String phone;
    private Integer role;
    private Integer createTime;
    private Integer updateTime;

    private String roleStr;

}
