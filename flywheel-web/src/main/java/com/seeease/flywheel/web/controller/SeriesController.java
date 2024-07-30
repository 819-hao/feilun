package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.ISeriesFacade;
import com.seeease.flywheel.goods.request.SeriesCreateRequest;
import com.seeease.flywheel.goods.request.SeriesDeleteRequest;
import com.seeease.flywheel.goods.request.SeriesPageRequest;
import com.seeease.flywheel.goods.request.SeriesUpdateRequest;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系列
 * @author Tiro
 * @date 2023/3/9
 */
@Slf4j
@RestController
@RequestMapping("/series")
public class SeriesController {
    @DubboReference(check = false, version = "1.0.0")
    private ISeriesFacade facade;

    /**
     * 查系列 列表
     *
     * @param request
     * @return
     */
    @PostMapping("/queryPage")
    public SingleResponse queryPage(@RequestBody SeriesPageRequest request) {
        return SingleResponse.of(facade.queryPage(request));
    }

    /**
     * 批量删除系列
     *
     * @param request
     * @return
     */
    @PostMapping("/batchDelete")
    public SingleResponse batchDelete(@RequestBody SeriesDeleteRequest request) {
        facade.batchDelete(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 修改系列
     *
     * @param request
     * @return
     */
    @PostMapping("/update")
    public SingleResponse update(@RequestBody SeriesUpdateRequest request) {
        facade.update(request);
        return SingleResponse.buildSuccess();
    }

    /**
     *创建系列
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public SingleResponse create(@RequestBody SeriesCreateRequest request) {
        facade.create(request);
        return SingleResponse.buildSuccess();
    }

}
