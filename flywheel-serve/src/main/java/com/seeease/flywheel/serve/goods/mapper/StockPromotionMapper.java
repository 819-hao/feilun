package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.StockPromotionListRequest;
import com.seeease.flywheel.goods.result.StockPromotionListResult;
import com.seeease.flywheel.goods.result.StockPromotionLogResult;
import com.seeease.flywheel.serve.goods.entity.StockPromotion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

/**
* @author edy
* @description 针对表【stock_promotion】的数据库操作Mapper
* @createDate 2023-07-18 14:00:34
* @Entity com.seeease.flywheel.serve.goods.entity.StockPromotion
*/
public interface StockPromotionMapper extends BaseMapper<StockPromotion> {

    Page<StockPromotionListResult> pageByRequest(Page<T> of ,@Param("request") StockPromotionListRequest request);

    Page<StockPromotionLogResult> pageOfLog(Page<Object> of,@Param("request") StockPromotionListRequest request);
}




