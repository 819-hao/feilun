package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.goods.request.SeriesPageRequest;
import com.seeease.flywheel.goods.result.SeriesPageResult;
import com.seeease.flywheel.serve.goods.entity.Series;
import com.seeease.flywheel.serve.goods.mapper.SeriesMapper;
import com.seeease.flywheel.serve.goods.service.SeriesService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【series(系列)】的数据库操作Service实现
 * @createDate 2023-01-31 18:50:22
 */
@Service
public class SeriesServiceImpl extends ServiceImpl<SeriesMapper, Series>
        implements SeriesService {

    @Override
    public Page<SeriesPageResult> queryPage(SeriesPageRequest request) {
        return this.baseMapper.queryPage(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public Integer getSeriesTypeByStockId(Integer stockId) {
        return this.baseMapper.getSeriesTypeByStockId(stockId);
    }

    @Override
    public Integer getSeriesTypeByGoodsId(Integer goodsId) {
        return this.baseMapper.getSeriesTypeByGoodsId(goodsId);
    }

    @Override
    public List<Series> listByName(List<String> nameList) {
        if (CollectionUtils.isEmpty(nameList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(Wrappers.<Series>lambdaQuery().
                in(Series::getName, nameList.stream().collect(Collectors.toSet())));
    }

}




