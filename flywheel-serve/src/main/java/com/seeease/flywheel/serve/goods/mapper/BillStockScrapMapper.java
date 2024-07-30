package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.request.ScrapOrderPageRequest;
import com.seeease.flywheel.goods.result.ScrapOrderPageResult;
import com.seeease.flywheel.serve.goods.entity.BillStockScrap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author edy
* @description 针对表【bill_stock_scrap(报废单)】的数据库操作Mapper
* @createDate 2023-12-20 16:47:56
* @Entity com.seeease.flywheel.serve.goods.entity.BillStockScrap
*/
public interface BillStockScrapMapper extends BaseMapper<BillStockScrap> {

    Page<ScrapOrderPageResult> queryScrapOrderPage(Page<Object> of,@Param("request") ScrapOrderPageRequest request);
}




