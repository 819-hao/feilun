package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.goods.request.GoodsWatchInfoRequest;
import com.seeease.flywheel.helper.result.MarketTrendsSearchResult;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【goods_watch(商品手表)】的数据库操作Service
 * @createDate 2023-01-31 18:50:12
 */
public interface GoodsWatchService extends IService<GoodsWatch> {

    List<WatchDataFusion> getWatchDataFusionListByGoodsIds(List<Integer> list);

    List<WatchDataFusion> getWatchDataFusionListByStockIds(List<Integer> stockIdList);

    /**
     * @param request
     * @return
     */
    Page<GoodsBaseInfo> listGoods(GoodsListRequest request);

    List<GoodsBaseInfo> listGoodsBaseInfo(List<String> brandNameList, List<String> simplifyModelList);

    /**
     * 稀蜴助手型号搜索
     * @param page
     * @param limit
     * @param q
     * @param model
     * @return
     */
    Page<MarketTrendsSearchResult> pageGoodsForHelperSearch(Integer page, Integer limit, String q, String model);

    void updateBrandBySeries(Integer id, Integer brandId);

    List<GoodsWatch> getAllList(GoodsWatchInfoRequest request);
}
