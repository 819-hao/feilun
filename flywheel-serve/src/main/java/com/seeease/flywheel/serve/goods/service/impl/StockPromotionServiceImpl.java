package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.goods.request.StockPromotionListRequest;
import com.seeease.flywheel.goods.result.StockPromotionListResult;
import com.seeease.flywheel.goods.result.StockPromotionLogResult;
import com.seeease.flywheel.serve.goods.entity.StockPromotion;
import com.seeease.flywheel.serve.goods.mapper.StockPromotionMapper;
import com.seeease.flywheel.serve.goods.service.StockPromotionService;
import org.springframework.stereotype.Service;

/**
 * @author edy
 * @description 针对表【stock_promotion】的数据库操作Service实现
 * @createDate 2023-07-18 14:00:34
 */
@Service
public class StockPromotionServiceImpl extends ServiceImpl<StockPromotionMapper, StockPromotion>
        implements StockPromotionService {

    @Override
    public Page<StockPromotionListResult> pageByRequest(StockPromotionListRequest request) {
        return this.baseMapper.pageByRequest(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Page<StockPromotionLogResult> pageOfLog(StockPromotionListRequest request) {
        return this.baseMapper.pageOfLog(Page.of(request.getPage(), request.getLimit()), request);
    }
}




