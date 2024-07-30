package com.seeease.flywheel.serve.stocktaking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktaking;
import com.seeease.flywheel.stocktaking.request.StocktakingSubmitRequest;

import java.util.Map;

/**
 * @author Tiro
 * @description 针对表【bill_stocktaking(盘点单)】的数据库操作Service
 * @createDate 2023-06-17 10:26:50
 */
public interface BillStocktakingService extends IService<BillStocktaking> {

    /**
     * 提交盘点
     *
     * @param request
     * @param wmoStockMap
     */
    void submit(StocktakingSubmitRequest request);
}
