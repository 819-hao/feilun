package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.goods.request.GoodsWatchInfoRequest;
import com.seeease.flywheel.helper.result.MarketTrendsSearchResult;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.mapper.GoodsWatchMapper;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【goods_watch(商品手表)】的数据库操作Service实现
 * @createDate 2023-01-31 18:50:12
 */
@Service
public class GoodsWatchServiceImpl extends ServiceImpl<GoodsWatchMapper, GoodsWatch>
        implements GoodsWatchService {


    @Override
    public List<WatchDataFusion> getWatchDataFusionListByGoodsIds(List<Integer> goodsIdList) {
        if (CollectionUtils.isEmpty(goodsIdList))
            return Collections.EMPTY_LIST;
        //去对lists 进行 削减 分组
        return Lists.partition(goodsIdList.stream()
                        .distinct()
                        .collect(Collectors.toList()), 200)
                .stream()
                .map(ids -> this.baseMapper.queryByGoodsIdList(ids))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<WatchDataFusion> getWatchDataFusionListByStockIds(List<Integer> stockIdList) {
        if (CollectionUtils.isEmpty(stockIdList))
            return Collections.EMPTY_LIST;
        //去对lists 进行 削减 分组
        return Lists.partition(stockIdList.stream()
                        .distinct()
                        .collect(Collectors.toList()), 200)
                .stream()
                .map(ids -> this.baseMapper.queryByStockIdList(ids))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Page<GoodsBaseInfo> listGoods(GoodsListRequest request) {
        return baseMapper.listGoods(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public List<GoodsBaseInfo> listGoodsBaseInfo(List<String> brandNameList, List<String> simplifyModelList) {
        if (CollectionUtils.isEmpty(brandNameList) && CollectionUtils.isEmpty(simplifyModelList)) {
            return Collections.EMPTY_LIST;
        }
        return baseMapper.listGoodsBaseInfo(brandNameList.stream().collect(Collectors.toSet()), simplifyModelList.stream().collect(Collectors.toSet()));
    }

    @Override
    public Page<MarketTrendsSearchResult> pageGoodsForHelperSearch(Integer page, Integer limit, String q, String model) {
        return baseMapper.pageGoodsForHelperSearch(Page.of(page, limit), q, model);
    }

    @Override
    public void updateBrandBySeries(Integer seriesId, Integer brandId) {
        baseMapper.updateBrandBySeries(seriesId, brandId);
    }

    @Override
    public List<GoodsWatch> getAllList(GoodsWatchInfoRequest request) {
        return baseMapper.getAllList(request);
    }
}




