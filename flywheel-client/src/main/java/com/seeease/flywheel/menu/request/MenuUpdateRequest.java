package com.seeease.flywheel.menu.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:44
 */
@Data
public class MenuUpdateRequest extends PageRequest {

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


}
