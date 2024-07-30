package com.seeease.flywheel.stocktaking;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.stocktaking.request.StocktakingDetailsRequest;
import com.seeease.flywheel.stocktaking.request.StocktakingListRequest;
import com.seeease.flywheel.stocktaking.request.StocktakingSubmitRequest;
import com.seeease.flywheel.stocktaking.result.*;

/**
 * 盘点服务
 *
 * @author Tiro
 * @date 2023/6/15
 */
public interface IStocktakingFacade {

    /**
     * 盘点仓库列表
     *
     * @return
     */
    StocktakingStoreListResult storeList();

    /**
     * 仓库库存
     *
     * @param storeId
     * @return
     */
    StocktakingStockListResult stockList(Integer storeId,String brand,String model);

    /**
     * 盘点提交
     *
     * @param request
     * @return
     */
    StocktakingSubmitResult stocktakingSubmit(StocktakingSubmitRequest request);

    /**
     * 盘点记录列表
     *
     * @param request
     * @return
     */
    PageResult<StocktakingListResult> list(StocktakingListRequest request);

    /**
     * 盘点详情
     *
     * @param request
     * @return
     */
    PageResult<StocktakingDetailsResult> details(StocktakingDetailsRequest request);


    /**
     * 盘点详情统计接口
     * @param request
     * @return
     */
    StocktakingDetailStatisticsResult detailsStatistics(StocktakingDetailsRequest request);
}
