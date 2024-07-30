package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.goods.request.ScrapStockPageRequest;
import com.seeease.flywheel.goods.request.ScrapTransitionAnomalyRequest;
import com.seeease.flywheel.goods.request.ScrappingStockRequest;
import com.seeease.flywheel.goods.result.ScrapStockPageResult;
import com.seeease.flywheel.serve.goods.entity.ScrapStock;
import com.seeease.flywheel.serve.goods.enums.ScrapStockStateEnum;

import java.util.List;

/**
* @author edy
* @description 针对表【scrap_stock(报废商品)】的数据库操作Service
* @createDate 2023-12-19 14:50:39
*/
public interface ScrapStockService extends IService<ScrapStock> {

    void scrappingStock(ScrappingStockRequest scrappingStockRequest);

    void scrapTransitionAnomaly(ScrapTransitionAnomalyRequest request);

    Page<ScrapStockPageResult> queryPage(ScrapStockPageRequest request);

    void updateStateByStockIds(List<Integer> stockIds, ScrapStockStateEnum stateEnum);
}
