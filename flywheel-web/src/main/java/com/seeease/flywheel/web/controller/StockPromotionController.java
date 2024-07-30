package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.goods.IStockPromotionFacade;
import com.seeease.flywheel.goods.request.*;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * 活动商品接口
 */
@Slf4j
@RestController
@RequestMapping("/stockPromotion")
public class StockPromotionController {

    @DubboReference(check = false, version = "1.0.0")
    private IStockPromotionFacade facade;

    /**
     *  查询活动商品列表
     * @param request
     * @return
     */
    @PostMapping("/queryStockPromotionList")
    public SingleResponse queryStockPromotionList(@RequestBody StockPromotionListRequest request) {
        return SingleResponse.of(facade.queryStockPromotionList(request));
    }


    /**
     *  查询活动商品下架日志
     * @param request
     * @return
     */
    @PostMapping("/log")
    public SingleResponse logs(@RequestBody StockPromotionListRequest request) {
        return SingleResponse.of(facade.logs(request));
    }


    /**
     * 批量上下架
     * @param request
     * @return
     */
    @PostMapping("/batchUpdateStatus")
    public SingleResponse updateStatus(@RequestBody StockPromotionBatchUpdateRequest request) {
        facade.batchUpdateStatus(request);
        return SingleResponse.buildSuccess();
    }
}
