package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 成员表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 帐号状态（0停用 1正常）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登陆时间
     */
    private Date loginDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private Date updateBy;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}