package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.goods.request.GoodsWatchInfoRequest;
import com.seeease.flywheel.helper.result.MarketTrendsSearchResult;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author dmmasxnmf
 * @description 针对表【goods_watch(商品手表)】的数据库操作Mapper
 * @createDate 2023-01-31 18:50:12
 * @Entity com.seeease.flywheel.GoodsWatch
 */
public interface GoodsWatchMapper extends BaseMapper<GoodsWatch> {
    List<WatchDataFusion> queryByGoodsIdList(@Param("goodsIdList") List<Integer> goodsIdList);

    List<WatchDataFusion> queryByStockIdList(@Param("stockIdList") List<Integer> stockIdList);

    Page<GoodsBaseInfo> listGoods(IPage<GoodsListRequest> page, @Param("request") GoodsListRequest request);

    List<GoodsBaseInfo> listGoodsBaseInfo(@Param("brandNameList") Set<String> brandNameList, @Param("simplifyModelList") Set<String> simplifyModelList);

    Page<MarketTrendsSearchResult> pageGoodsForHelperSearch(Page<Object> page,
                                                            @Param("q") String q,
                                                            @Param("model") String model);

    void updateBrandBySeries(@Param("seriesId") Integer seriesId, @Param("brandId") Integer brandId);

    List<GoodsWatch> getAllList(@Param("request") GoodsWatchInfoRequest request);
}




