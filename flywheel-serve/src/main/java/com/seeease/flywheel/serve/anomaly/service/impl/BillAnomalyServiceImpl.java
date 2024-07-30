package com.seeease.flywheel.serve.anomaly.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.anomaly.request.AnomalyListRequest;
import com.seeease.flywheel.anomaly.result.AnomalyListResult;
import com.seeease.flywheel.serve.anomaly.entity.BillAnomaly;
import com.seeease.flywheel.serve.anomaly.mapper.BillAnomalyMapper;
import com.seeease.flywheel.serve.anomaly.service.BillAnomalyService;
import com.seeease.flywheel.serve.goods.entity.GoodsWatch;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_anomaly(异常单)】的数据库操作Service实现
 * @createDate 2023-04-12 14:35:07
 */
@Service
public class BillAnomalyServiceImpl extends ServiceImpl<BillAnomalyMapper, BillAnomaly>
        implements BillAnomalyService {

    @Resource
    private GoodsWatchService goodsWatchService;

    @Override
    public PageResult<AnomalyListResult> list(AnomalyListRequest request) {

        if (CollectionUtils.isNotEmpty(request.getBrandIdList())) {

            List<Integer> collect = goodsWatchService.list(Wrappers.<GoodsWatch>lambdaQuery()
                    .in(GoodsWatch::getBrandId, request.getBrandIdList())).stream().map(GoodsWatch::getId).collect(Collectors.toList());

            request.setGoodsIdList(CollectionUtils.isNotEmpty(collect) ? collect : null);
        }

        request.setAnomalyState(Optional.ofNullable(request.getAnomalyState())
                .filter(v -> v != -1)
                .orElse(null));

        Page<AnomalyListResult> page = baseMapper.list(new Page<>(request.getPage(), request.getLimit()), request);

        List<AnomalyListResult> records = page.getRecords();

        List<WatchDataFusion> fusionList = goodsWatchService.getWatchDataFusionListByGoodsIds(records.stream().map(AnomalyListResult::getGoodsId).distinct().collect(Collectors.toList()));

        records.forEach(pricingListResult -> {
            WatchDataFusion watchDataFusion = fusionList.stream().filter(r -> r.getGoodsId().equals(pricingListResult.getGoodsId())).findAny().get();
            pricingListResult.setBrandName(watchDataFusion.getBrandName());
            pricingListResult.setSeriesName(watchDataFusion.getSeriesName());
            pricingListResult.setModel(watchDataFusion.getModel());
            pricingListResult.setImage(watchDataFusion.getImage());
        });

        return PageResult.<AnomalyListResult>builder()
                .result(records)
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();

    }
}




