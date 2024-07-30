package com.seeease.flywheel.serve.menu.rpc;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.menu.IMenuFacade;
import com.seeease.flywheel.menu.request.MenuCreateRequest;
import com.seeease.flywheel.menu.request.MenuListRequest;
import com.seeease.flywheel.menu.request.MenuUpdateRequest;
import com.seeease.flywheel.menu.result.MenuListResult;
import com.seeease.flywheel.menu.result.MenuParentListResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.menu.convert.MenuConverter;
import com.seeease.flywheel.serve.menu.entity.Menu;
import com.seeease.flywheel.serve.menu.service.IMenuService;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther Gilbert
 * @Date 2023/10/24 14:41
 */
@DubboService(version = "1.0.0")
@Slf4j
public class MenuFacade implements IMenuFacade {
    @Resource
    private IMenuService menuService;

    @Override
    public PageResult<MenuListResult> queryPageList(MenuListRequest request) {

        Page<Menu> page = menuService.page(request);
        return PageResult.<MenuListResult>builder()
                .result(page.getRecords()
                        .stream()
                        .map(t-> MenuConverter.INSTANCE.convertQueryResult(t)).collect(Collectors.toList()))
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }

    @Override
    public List<MenuListResult> list(MenuListRequest request) {
        List<Menu> list = menuService.list(request);
        return list.stream()
                .map(t -> MenuConverter.INSTANCE.convertQueryResult(t)).collect(Collectors.toList());
    }

    @Override
    public int create(MenuCreateRequest request) {
        if(StringUtils.isEmpty(request.getName())
                || request.getParentId() == null
                || StringUtils.isEmpty(request.getRouter())
                ||  StringUtils.isEmpty(request.getMenuType())){

            throw new OperationRejectedException(OperationExceptionCode.MENU_MUST_FILL_IN);
        }
        return menuService.create(request);
    }

    @Override
    public int update(MenuUpdateRequest request) {
        return menuService.update(request);
    }

    @Override
    public List<MenuParentListResult> getParentName() {
        List<MenuParentListResult> listResultList = Lists.newArrayList();
        List<Menu> parentName = menuService.getParentName();
        //获取中暑进行
        Map<Integer, Menu> menuMap = parentName.stream().collect(Collectors.toMap(Menu::getId, a -> a, (k1, k2) -> k1));
        if(CollectionUtil.isNotEmpty(parentName)){
            //进行去重
            List<Menu> treeSet = parentName.stream().filter(f->StringUtils.isNotEmpty(f.getName())).collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(
                            Comparator.comparing(
                                    Menu::getParentId))), ArrayList::new));

            if(CollectionUtil.isNotEmpty(treeSet)){
                //循环去重数据
                treeSet.forEach(v->{
                    Menu orDefault = menuMap.getOrDefault(v.getParentId(), null);
                    if(Objects.nonNull(orDefault)){
                        MenuParentListResult menuParentListResult = new MenuParentListResult();
                        menuParentListResult.setParentId(orDefault.getId());
                        menuParentListResult.setName(orDefault.getName());
                        listResultList.add(menuParentListResult);
                    }
                });
            }
        }
        return listResultList;
    }

    @Override
    public int removeById(Integer id) {
        return menuService.removeById(id);
    }
}
