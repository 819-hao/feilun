package com.seeease.flywheel.serve.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:36
 */
@TableName(value = "permission")
@Data
@Accessors(chain = true)
public class Permission implements Serializable  {


    private static final long serialVersionUID = 1L;
    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限类型 1 字段权限 2资源权限  3 入口
     */
    private Integer type;

    /**
     * 资源菜单id
     */
    private Integer menuId;

    /**
     * 菜单目录id
     */
    private Integer menuParentId;

    /**
     * 数据权限控制字段
     */
    private String code;
    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Integer deleted;
}