package com.seeease.flywheel.menu.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:44
 */
@Data
public class MenuListRequest extends PageRequest {

    /**
     * 菜单名称
     */
    public String name;


}
