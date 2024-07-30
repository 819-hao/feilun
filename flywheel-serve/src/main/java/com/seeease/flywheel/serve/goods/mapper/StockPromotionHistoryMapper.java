package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.request.StockPromotionListRequest;
import com.seeease.flywheel.goods.result.StockPromotionListResult;
import com.seeease.flywheel.serve.goods.entity.StockPromotion;
import com.seeease.flywheel.serve.goods.entity.StockPromotionHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;


public interface StockPromotionHistoryMapper extends BaseMapper<StockPromotionHistory> {

}




