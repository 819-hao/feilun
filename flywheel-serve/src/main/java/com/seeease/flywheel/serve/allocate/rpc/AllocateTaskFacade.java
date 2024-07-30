package com.seeease.flywheel.serve.allocate.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.allocate.IAllocateTaskFacade;
import com.seeease.flywheel.allocate.request.AllocateTaskListRequest;
import com.seeease.flywheel.allocate.result.AllocateTaskListResult;
import com.seeease.flywheel.serve.allocate.convert.AllocateConverter;
import com.seeease.flywheel.serve.allocate.entity.BillAllocateTask;
import com.seeease.flywheel.serve.allocate.service.BillAllocateTaskService;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.springframework.context.UserContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/8/29
 */
@DubboService(version = "1.0.0")
public class AllocateTaskFacade implements IAllocateTaskFacade {
    @Resource
    private BillAllocateTaskService billAllocateTaskService;
    @Resource
    private StoreManagementService storeManagementService;
    @Resource
    private StockService stockService;
    @Resource
    private GoodsWatchService goodsWatchService;

    @Override
    public PageResult<AllocateTaskListResult> list(AllocateTaskListRequest request) {
        LambdaQueryWrapper<BillAllocateTask> wrapper = Wrappers.<BillAllocateTask>lambdaQuery()
                .orderByDesc(BillAllocateTask::getId)
                .eq(Objects.nonNull(request.getTaskState()) && request.getTaskState() != -1, BillAllocateTask::getTaskState, request.getTaskState())
                .in(StringUtils.isNoneBlank(request.getStockSn()), BillAllocateTask::getStockId,
                        stockService.findByStockSn(request.getStockSn()).stream().map(Stock::getId).collect(Collectors.toList()))
                .eq(StringUtils.isNoneBlank(request.getCreatedBy()), BillAllocateTask::getCreatedBy, request.getCreatedBy())
                .eq(StringUtils.isNoneBlank(request.getAllocateNo()), BillAllocateTask::getAllocateNo, request.getAllocateNo())
                //时间范围
                .between(StringUtils.isNotEmpty(request.getStartTime()) && StringUtils.isNotEmpty(request.getEndTime()),
                        BillAllocateTask::getCreatedTime, request.getStartTime(), request.getEndTime());
        Integer storeId = UserContext.getUser().getStore().getId();
        if (storeId != FlywheelConstant._ZB_ID) {
            wrapper.eq(BillAllocateTask::getFromId, storeId)
                    .or()
                    .eq(BillAllocateTask::getToId, storeId);
        } else {
            wrapper.eq(Objects.nonNull(request.getFromId()) && request.getFromId() != -1, BillAllocateTask::getFromId, request.getFromId())
                    .eq(Objects.nonNull(request.getToId()) && request.getToId() != -1, BillAllocateTask::getToId, request.getToId());
        }
        Page<BillAllocateTask> pageResult = billAllocateTaskService.page(Page.of(request.getPage(), request.getLimit()), wrapper);


        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResult.<AllocateTaskListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .build();
        }

        //调出调入方
        Map<Integer, String> tagList = storeManagementService.selectInfoByIds(pageResult.getRecords().stream()
                        .map(t -> Lists.newArrayList(t.getFromId(), t.getToId()))
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));

        //型号信息
        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByGoodsIds(pageResult.getRecords().stream()
                        .map(BillAllocateTask::getGoodsId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(WatchDataFusion::getGoodsId, Function.identity()));

        Map<Integer, StockExt> stockMap = Optional.ofNullable(pageResult.getRecords().stream().map(BillAllocateTask::getStockId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .filter(CollectionUtils::isNotEmpty)
                .map(ids -> stockService.selectByStockIdList(ids)
                        .stream()
                        .collect(Collectors.toMap(StockExt::getStockId, Function.identity())))
                .orElse(Collections.EMPTY_MAP);

        return PageResult.<AllocateTaskListResult>builder()
                .result(pageResult.getRecords()
                        .stream()
                        .map(t -> {
                            AllocateTaskListResult r = AllocateConverter.INSTANCE.convertAllocateTaskListResult(t);
                            r.setFromName(tagList.get(t.getFromId())); //调出方
                            r.setToName(tagList.get(t.getToId())); //调入方

                            StockExt stockExt = stockMap.get(t.getStockId());
                            if (Objects.nonNull(stockExt)) {
                                r.setStockSn(stockExt.getStockSn());
                                r.setStockStatus(stockExt.getStockStatus());
                            }
                            WatchDataFusion goods = goodsMap.get(t.getGoodsId());
                            if (Objects.nonNull(goods)) {
                                r.setBrandName(goods.getBrandName());
                                r.setSeriesName(goods.getSeriesName());
                                r.setModel(goods.getModel());
                                r.setImage(goods.getImage());
                            }
                            return r;
                        })
                        .collect(Collectors.toList())
                )
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }
}
