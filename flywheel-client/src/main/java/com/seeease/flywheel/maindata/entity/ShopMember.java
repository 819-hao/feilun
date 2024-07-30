package com.seeease.flywheel.maindata.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class ShopMember implements Serializable {
    /**
     * 店铺id
     */
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
     * 性别:1-男，2-女
     */
    private Integer gender;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 角色key
     */
    private String roleKey;

    /**
     * 角色名称
     */
    private String roleName;
}
