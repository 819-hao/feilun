package com.seeease.flywheel.goods;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.ScrapOrderDetailResult;
import com.seeease.flywheel.goods.result.ScrapOrderPageResult;
import com.seeease.flywheel.goods.result.ScrapStockPageResult;

/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface IScrapStockFacade {

    void scrappingStock(ScrappingStockRequest scrappingStockRequest);

    PageResult<ScrapStockPageResult> queryPage(ScrapStockPageRequest request);

    void scrapTransitionAnomaly(ScrapTransitionAnomalyRequest request);

    void scrapStorage(ScrapStorageRequest request);

    PageResult<ScrapOrderPageResult> queryScrapOrderPage(ScrapOrderPageRequest request);

    ScrapOrderDetailResult queryScrapOrderDetail(ScrapOrderDetailRequest request);
}
