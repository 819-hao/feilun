package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IScrapStockFacade;
import com.seeease.flywheel.goods.request.*;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报废商品接口
 *
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/28 14:39
 */
@Slf4j
@RestController
@RequestMapping("/scrapStock")
public class ScrapStockController {

    @DubboReference(check = false, version = "1.0.0")
    private IScrapStockFacade facade;


    /**
     * 异常商品报废
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/scrappingStock")
    public SingleResponse scrappingStock(@RequestBody ScrappingStockRequest request) {
        facade.scrappingStock(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 查询报废列表
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/queryPage")
    public SingleResponse queryPage(@RequestBody ScrapStockPageRequest request) {
        return SingleResponse.of(facade.queryPage(request));
    }

    /**
     * 报废转异常商品
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/scrapTransitionAnomaly")
    public SingleResponse scrapTransitionAnomaly(@RequestBody ScrapTransitionAnomalyRequest request) {
        facade.scrapTransitionAnomaly(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 报废出库
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/scrapStorage")
    public SingleResponse scrapStorage(@RequestBody ScrapStorageRequest request) {
        facade.scrapStorage(request);
        return SingleResponse.buildSuccess();
    }

    /**
     * 查询报废订单列表
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/queryScrapOrderPage")
    public SingleResponse queryScrapOrderPage(@RequestBody ScrapOrderPageRequest request) {
        return SingleResponse.of(facade.queryScrapOrderPage(request));
    }

    /**
     * 报废订单详情
     *
     * @param request
     * @return
     */
    @PostMapping("/pc/queryScrapOrderDetail")
    public SingleResponse queryScrapOrderDetail(@RequestBody ScrapOrderDetailRequest request) {
        return SingleResponse.of(facade.queryScrapOrderDetail(request));
    }
}
