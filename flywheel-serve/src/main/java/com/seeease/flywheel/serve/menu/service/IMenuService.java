package com.seeease.flywheel.serve.menu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.menu.request.MenuCreateRequest;
import com.seeease.flywheel.menu.request.MenuListRequest;
import com.seeease.flywheel.menu.request.MenuUpdateRequest;
import com.seeease.flywheel.serve.menu.entity.Menu;

import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:41
 */
public interface IMenuService extends IService<Menu> {
    Page<Menu> page(MenuListRequest request);

    List<Menu> list(MenuListRequest request);

    int create(MenuCreateRequest request);
    int update(MenuUpdateRequest request);

    List<Menu> getParentName();

    int removeById(Integer id);
}
