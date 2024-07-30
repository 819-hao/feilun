package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.StockPromotionListRequest;
import com.seeease.flywheel.goods.result.StockPromotionListResult;
import com.seeease.flywheel.goods.result.StockPromotionLogResult;
import com.seeease.flywheel.serve.goods.entity.StockPromotion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author edy
 * @description 针对表【stock_promotion】的数据库操作Service
 * @createDate 2023-07-18 14:00:34
 */
public interface StockPromotionService extends IService<StockPromotion> {

    Page<StockPromotionListResult> pageByRequest(StockPromotionListRequest request);

    Page<StockPromotionLogResult> pageOfLog(StockPromotionListRequest request);
}