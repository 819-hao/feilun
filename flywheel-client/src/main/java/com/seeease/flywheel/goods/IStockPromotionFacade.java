package com.seeease.flywheel.goods;


import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.StockPromotionImportResult;
import com.seeease.flywheel.goods.result.StockPromotionInfo;
import com.seeease.flywheel.goods.result.StockPromotionListResult;
import com.seeease.flywheel.goods.result.StockPromotionLogResult;

/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface IStockPromotionFacade {


    PageResult<StockPromotionListResult> queryStockPromotionList(StockPromotionListRequest request);

    void batchUpdateStatus(StockPromotionBatchUpdateRequest request);

    ImportResult<StockPromotionImportResult> stockQueryImport(StockPromotionImportRequest request);

    void stockQueryTakeDownImport(StockPromotionTakeDownImportRequest request);

    PageResult<StockPromotionLogResult> logs(StockPromotionListRequest request);

    StockPromotionInfo get(StockPromotionInfoGetRequest request);
}
