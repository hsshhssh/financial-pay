package com.xqh.financial.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "xqh_role")
public class XqhRole {
    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 角色名称
     */
    @Column(name = "role_name")
    private String roleName;
}