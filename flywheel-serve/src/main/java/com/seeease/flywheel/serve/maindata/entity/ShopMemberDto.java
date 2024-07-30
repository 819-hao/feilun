package com.seeease.flywheel.serve.maindata.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/2/18
 */
@Data
public class ShopMemberDto implements Serializable {
    private Integer shopId;

    /**
     * 企微用户id
     */
    private String userid;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 角色key
     */
    private String roleKeys;

    /**
     * 角色名称
     */
    private String roleNames;

    private Integer id;
}
