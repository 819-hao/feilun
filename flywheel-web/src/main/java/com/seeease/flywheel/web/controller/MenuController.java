package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.menu.IMenuFacade;
import com.seeease.flywheel.menu.request.MenuCreateRequest;
import com.seeease.flywheel.menu.request.MenuListRequest;
import com.seeease.flywheel.menu.request.MenuUpdateRequest;
import com.seeease.flywheel.menu.result.MenuListResult;
import com.seeease.flywheel.menu.result.MenuParentListResult;
import com.seeease.flywheel.pricing.request.PricingListRequest;
import com.seeease.flywheel.rfid.result.RfidWmsCollectDetailResult;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理
 * @Auther Gilbert
 * @Date 2023/10/23 16:53
 */
@Slf4j
@RestController
@RequestMapping("menu")
public class MenuController {


    @DubboReference(check = false, version = "1.0.0")
    private IMenuFacade menuFacade;


    /**
     * 分页查询菜单列表
     * @param request
     * @return
     */
    @PostMapping("/queryPageList")
    public SingleResponse<PageResult<MenuListResult>> queryPageList(@RequestBody MenuListRequest request) {
        return SingleResponse.of(menuFacade.queryPageList(request));
    }

    /**
     * 查询菜单列表
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse<List<MenuListResult>> list(@RequestBody MenuListRequest request) {
        return SingleResponse.of(menuFacade.list(request));
    }

    /**
     * 保存菜单
     * @param request
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody MenuCreateRequest request){
        return SingleResponse.of(menuFacade.create(request));
    }

    /**
     * 修改菜单
     * @param request
     * @return
     */
    @PostMapping("/update")
    public SingleResponse update(@RequestBody MenuUpdateRequest request){
        return SingleResponse.of(menuFacade.update(request));
    }

    /**
     * 获取所有父菜单信息
     * @return
     */
    @GetMapping("/getParentName")
    public SingleResponse<List<MenuParentListResult>> parentName(){
        return SingleResponse.of(menuFacade.getParentName());
    }

    /**
     * 删除菜单接口
     * @param id
     * @return
     */
    @GetMapping("/remove/{id}")
    public SingleResponse remove(@PathVariable("id") Integer id){

        return SingleResponse.of(menuFacade.removeById(id));

    }
}
