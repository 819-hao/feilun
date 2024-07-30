package com.seeease.flywheel.menu;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.menu.request.MenuCreateRequest;
import com.seeease.flywheel.menu.request.MenuListRequest;
import com.seeease.flywheel.menu.request.MenuUpdateRequest;
import com.seeease.flywheel.menu.result.MenuListResult;
import com.seeease.flywheel.menu.result.MenuParentListResult;

import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/10/23 16:54
 */
public interface IMenuFacade {
    PageResult<MenuListResult> queryPageList(MenuListRequest request);

    List<MenuListResult> list(MenuListRequest request);

    int create(MenuCreateRequest request);

    int update(MenuUpdateRequest request);

    List<MenuParentListResult> getParentName();

    int removeById(Integer id);
}
