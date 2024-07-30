package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.request.SeriesPageRequest;
import com.seeease.flywheel.goods.result.SeriesPageResult;
import com.seeease.flywheel.serve.goods.entity.Series;
import org.apache.ibatis.annotations.Param;

/**
* @author dmmasxnmf
* @description 针对表【series(系列)】的数据库操作Mapper
* @createDate 2023-01-31 18:50:22
* @Entity com.seeease.flywheel.Series
*/
public interface SeriesMapper extends BaseMapper<Series> {

    Page<SeriesPageResult> queryPage(Page<Object> of,@Param("request") SeriesPageRequest request);

    Integer getSeriesTypeByStockId(@Param("stockId") Integer stockId);

    Integer getSeriesTypeByGoodsId(@Param("goodsId") Integer goodsId);
}




