package com.seeease.flywheel.maindata.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Data
public class UserInfo implements Serializable {
    /**
     * 用户id
     */
    private Integer id;

    /**
     * 第三方企微ID（唯一）
     */
    private String userid;

    /**
     * 企微成员所属部门id列表
     */
    private String department;

    /**
     * 成员名称
     */
    private String name;

    /**
     * 成员别名
     */
    private String alias;

    /**
     * 邮箱。长度6~64个字节，且为有效的email格式。企业内必须唯一，mobile/email二者不能同时为空
     */
    private String email;

    /**
     * 手机号码。企业内必须唯一，mobile/email二者不能同时为空
     */
    private String mobile;

    /**
     * 性别。1表示男性，2表示女性
     */
    private String gender;

    /**
     * 成员头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 岗位
     */
    private String position;

    /**
     * 地址
     */
    private String address;

    /**
     *
     */
    private String openUserid;

    /**
     * 1 个人数据 2代表部门
     */
    private Integer dataPerm;

    /**
     * 附加数据
     */
    private String extAttrsStr;

    /**
     * 工号
     */
    private String jobNumber;
}
