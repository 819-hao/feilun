package com.seeease.flywheel.serve.goods.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IStockLifeCycleFacade;
import com.seeease.flywheel.goods.request.StockExceptionListRequest;
import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.goods.request.StockLifecycleListRequest;
import com.seeease.flywheel.goods.result.StockExceptionListResult;
import com.seeease.flywheel.goods.result.StockLifeCycleListResult;
import com.seeease.flywheel.serve.goods.convert.StockLifeCycleConverter;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;
import com.seeease.flywheel.serve.goods.service.BillLifeCycleService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.qt.entity.BillQualityTesting;
import com.seeease.flywheel.serve.qt.service.BillQualityTestingService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 15:46
 */
@DubboService(version = "1.0.0")
public class StockLifecycleFacade implements IStockLifeCycleFacade {

    @Resource
    private BillLifeCycleService billLifeCycleService;

    @Resource
    private StockService stockService;

    @Resource
    private BillQualityTestingService billQualityTestingService;

    @Override
    public void createBatch(List<StockLifeCycleCreateRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            return;
        }
        List<BillLifeCycle> collect = request.stream()
                .map(stockLifeCycleCreateRequest -> StockLifeCycleConverter.INSTANCE.convert(stockLifeCycleCreateRequest))
                .collect(Collectors.toList());

        billLifeCycleService.insertBatchSomeColumn(collect);
    }

    @Override
    public PageResult<StockLifeCycleListResult> list(StockLifecycleListRequest request) {

        Page<BillLifeCycle> page = billLifeCycleService.page(new Page<>(request.getPage(), request.getLimit()),
                Wrappers.<BillLifeCycle>lambdaQuery().in(BillLifeCycle::getStockId, request.getStockId())
                        .orderByDesc(BillLifeCycle::getOperationTime));

        List<StockLifeCycleListResult> resultList = page.getRecords().stream().map(billLifeCycle -> {

            StockLifeCycleListResult stockLifeCycleListResult = StockLifeCycleConverter.INSTANCE.convertStockLifecycleResult(billLifeCycle);

            return stockLifeCycleListResult;
        }).collect(Collectors.toList());

        return PageResult.<StockLifeCycleListResult>builder()
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .result(resultList)
                .build();
    }

    @Override
    public PageResult<StockExceptionListResult> exceptionStock(StockExceptionListRequest request) {

        PageResult<StockExceptionListResult> result = stockService.exceptionStock(request);

        if (CollectionUtils.isEmpty(result.getResult())) {
            return result;
        }

        Map<Integer, List<BillQualityTesting>> collect = billQualityTestingService.list(Wrappers.<BillQualityTesting>lambdaQuery()
                        .in(BillQualityTesting::getStockId, result.getResult().stream().map(StockExceptionListResult::getStockId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(BillQualityTesting::getStockId));

        for (StockExceptionListResult stockExceptionListResult : result.getResult()) {

            List<BillQualityTesting> billQualityTestingList = collect.get(stockExceptionListResult.getStockId());
            if (collect.containsKey(stockExceptionListResult.getStockId()) && CollectionUtils.isNotEmpty(billQualityTestingList)) {

                stockExceptionListResult.setBqtExceptionReason(billQualityTestingList.get(billQualityTestingList.size() - FlywheelConstant.ONE).getExceptionReason());
            }
        }
        return result;
    }
}
