package com.seeease.flywheel.web.controller;

import com.seeease.flywheel.stocktaking.IStocktakingFacade;
import com.seeease.flywheel.stocktaking.request.StocktakingDetailsRequest;
import com.seeease.flywheel.stocktaking.request.StocktakingListRequest;
import com.seeease.flywheel.stocktaking.request.StocktakingSubmitRequest;
import com.seeease.flywheel.stocktaking.result.StocktakingDetailStatisticsResult;
import com.seeease.flywheel.stocktaking.result.StocktakingStockListResult;
import com.seeease.flywheel.stocktaking.result.StocktakingStoreListResult;
import com.seeease.springframework.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

/**
 * 库存盘点
 *
 * @author Tiro
 * @date 2023/6/15
 */
@Slf4j
@RestController
@RequestMapping("/stocktaking")
public class StocktakingController {

    @DubboReference(check = false, version = "1.0.0")
    private IStocktakingFacade stocktakingFacade;

    /**
     * 仓库列表
     *
     * @param
     * @return
     */
    @GetMapping("/storeList")
    public SingleResponse storeList() {
        return SingleResponse.of(Optional.ofNullable(stocktakingFacade.storeList())
                .map(StocktakingStoreListResult::getStoreList)
                .orElse(Collections.emptyList()));
    }

    /**
     * 仓库库存
     *
     * @param storeId
     * @return
     */
    @GetMapping("/stockList/{storeId}")
    public SingleResponse stockList(@PathVariable Integer storeId,
                                    @RequestParam(required = false) String brand,
                                    @RequestParam(required = false) String model) {
        return SingleResponse.of(Optional.ofNullable(stocktakingFacade. stockList(storeId,brand,model))
                .map(StocktakingStockListResult::getStockList)
                .orElse(Collections.emptyList()));
    }

    /**
     * 盘点提交
     *
     * @param request
     * @return
     */
    @PostMapping("/submit")
    public SingleResponse stocktakingSubmit(@RequestBody StocktakingSubmitRequest request) {
        Assert.notNull(request, "参数不能为空");
        Assert.notNull(request.getQuantity(), "盘点数量不能为空");
        return SingleResponse.of(stocktakingFacade.stocktakingSubmit(request));
    }

    /**
     * 盘点列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list")
    public SingleResponse list(@RequestBody StocktakingListRequest request) {
        Assert.notNull(request, "参数不能为空");
        return SingleResponse.of(stocktakingFacade.list(request));
    }

    /**
     * 盘点详情统计接口
     * @param request
     * @return
     */
    @PostMapping("details/statistics")
    public SingleResponse<StocktakingDetailStatisticsResult> detailsStatistics(@RequestBody StocktakingDetailsRequest request) {
        Assert.notNull(request, "参数不能为空");
        Assert.notNull(request.getId(), "id不能为空");
        return SingleResponse.of(stocktakingFacade.detailsStatistics(request));
    }

    /**
     * 盘点详情
     *
     * @param request
     * @return
     */
    @PostMapping("/details")
    public SingleResponse details(@RequestBody StocktakingDetailsRequest request) {
        Assert.notNull(request, "参数不能为空");
        Assert.notNull(request.getId(), "id不能为空");
        return SingleResponse.of(stocktakingFacade.details(request));
    }
}
