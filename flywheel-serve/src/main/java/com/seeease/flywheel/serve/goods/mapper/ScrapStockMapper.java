package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.request.ScrapStockPageRequest;
import com.seeease.flywheel.goods.result.ScrapStockPageResult;
import com.seeease.flywheel.serve.goods.entity.ScrapStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author edy
 * @description 针对表【scrap_stock(报废商品)】的数据库操作Mapper
 * @createDate 2023-12-19 14:50:39
 * @Entity com.seeease.flywheel.serve.goods.entity.ScrapStock
 */
public interface ScrapStockMapper extends BaseMapper<ScrapStock> {

    Page<ScrapStockPageResult> queryPage(Page<Object> of, @Param("request") ScrapStockPageRequest request);

    void updateStateByStockIds(@Param("stockIds") List<Integer> stockIds, @Param("state") Integer state);
}




