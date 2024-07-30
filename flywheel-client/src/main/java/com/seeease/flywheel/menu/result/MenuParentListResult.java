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
public class MenuParentListResult implements Serializable {

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Integer parentId;
}
