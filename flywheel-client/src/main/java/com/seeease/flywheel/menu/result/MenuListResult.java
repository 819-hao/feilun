package com.seeease.flywheel.menu.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuListResult implements Serializable {
    /**
     * 菜单ID
     */
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Integer parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 组件路径+路由
     */
    private String path;

    /**
     * 注册路由
     */
    private String router;

    /**
     * 是否为外链（0是 1否）
     */
    private Integer isFrame;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private Integer invisible;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 菜单状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

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
     * 备注
     */
    private String remark;

    /**
     *
     */
    private String authority;
}
