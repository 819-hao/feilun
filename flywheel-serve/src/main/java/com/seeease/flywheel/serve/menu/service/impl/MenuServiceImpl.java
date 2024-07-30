package com.seeease.flywheel.serve.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.menu.request.MenuCreateRequest;
import com.seeease.flywheel.menu.request.MenuListRequest;
import com.seeease.flywheel.menu.request.MenuUpdateRequest;
import com.seeease.flywheel.serve.menu.convert.MenuConverter;
import com.seeease.flywheel.serve.menu.entity.Menu;
import com.seeease.flywheel.serve.menu.mapper.MenuMapper;
import com.seeease.flywheel.serve.menu.service.IMenuService;
import com.seeease.flywheel.serve.permission.entity.Permission;
import com.seeease.flywheel.serve.permission.service.IPermissionService;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.springframework.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:42
 */
@Service
@Slf4j
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Resource
    private MenuMapper menuMapper;
    @Resource
    private IPermissionService permissionService;

    @Override
    public Page<Menu> page(MenuListRequest request) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(request.getName()),Menu::getName,request.getName())
                .isNotNull(Menu::getName)
                .ne(Menu::getName,"")
                .eq(Menu::getDeleted,WhetherEnum.NO.getValue())
                .orderByAsc(Menu::getParentId,Menu::getOrderNum);
        Page<Menu> billPricingPage = menuMapper.selectPage(new Page<>(request.getPage(), request.getLimit()), wrapper);
        return billPricingPage;
    }

    @Override
    public List<Menu> list(MenuListRequest request) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(request.getName()),Menu::getName,request.getName())
                .isNotNull(Menu::getName)
                .ne(Menu::getName,"")
                .eq(Menu::getDeleted,WhetherEnum.NO.getValue())
                .orderByAsc(Menu::getParentId,Menu::getOrderNum);
        return menuMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(MenuCreateRequest request) {
        Menu menu = MenuConverter.INSTANCE.convertDO(request);
        menu.setCreateBy(UserContext.getUser().getUserName());
        int insert = menuMapper.insert(menu);
        //如果权限表中有重复名字，名字就置为菜单名字+id
        if(Objects.nonNull(permissionByName(menu.getName(),null))){
            menu.setName(menu.getName()+"-"+menu.getId());
        }
        //保存权限表
        permissionService.save(new Permission()
                .setName(menu.getName())
                .setType(2)//2是页面路由
                .setMenuId(menu.getId())
                .setMenuParentId(menu.getParentId())
        );
        return insert;
    }
    //查询权限中是否有重复菜单名字
    public Permission permissionByName(String menuName,Integer menuId){
        LambdaUpdateWrapper<Permission> permissionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        permissionLambdaUpdateWrapper.eq(Permission::getName,menuName)
                .ne(menuId !=null,Permission::getMenuId,menuId);
        return permissionService.getOne(permissionLambdaUpdateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(MenuUpdateRequest request) {
        Menu menu = MenuConverter.INSTANCE.convertUpdateDO(request);
        menu.setUpdateBy(UserContext.getUser().getUserName());
        menu.setUpdateTime(new Date());
        if(Objects.nonNull(permissionByName(menu.getName(),menu.getId()))){
            menu.setName(menu.getName()+"-"+menu.getId());
        }
        //修改权限名字
        LambdaUpdateWrapper<Permission> permissionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        permissionLambdaUpdateWrapper.set(Permission::getName,menu.getName())
                .eq(Permission::getMenuId,menu.getId());
        permissionService.update(null,permissionLambdaUpdateWrapper);
        return menuMapper.updateById(menu);
    }

    @Override
    public List<Menu> getParentName() {

        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getStatus,0)
                .isNotNull(Menu::getName)
                .ne(Menu::getName,"");
        List<Menu> menus = menuMapper.selectList(wrapper);
        return menus;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeById(Integer id) {
        //修改权限名字
        LambdaUpdateWrapper<Permission> permissionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        permissionLambdaUpdateWrapper.set(Permission::getDeleted, WhetherEnum.YES.getValue())
                .eq(Permission::getMenuId,id);
        permissionService.update(null,permissionLambdaUpdateWrapper);

        LambdaUpdateWrapper<Menu> menuLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        menuLambdaUpdateWrapper.set(Menu::getDeleted, WhetherEnum.YES.getValue())
                .set(Menu::getStatus,WhetherEnum.YES.getValue())
                .eq(Menu::getId,id);
        return menuMapper.update(null,menuLambdaUpdateWrapper);
    }
}
