package com.seeease.flywheel.serve.goods.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.account.result.AccountQueryResult;
import com.seeease.flywheel.goods.IStockLogFacade;
import com.seeease.flywheel.goods.request.LogStockOptListRequest;
import com.seeease.flywheel.goods.result.LogStockOptListResult;
import com.seeease.flywheel.serve.goods.convert.LogStockOptConverter;
import com.seeease.flywheel.serve.goods.entity.LogStockOpt;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.service.LogStockOptService;
import com.seeease.flywheel.serve.goods.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/3/19 14:30
 */
@Slf4j
@DubboService(version = "1.0.0")
public class StockLogFacade implements IStockLogFacade {

    @Resource
    private LogStockOptService logStockOptService;

    @Resource
    private StockService stockService;

    @Override
    public PageResult<LogStockOptListResult> list(LogStockOptListRequest request) {

        Page<LogStockOpt> page = logStockOptService.page(new Page<>(request.getPage(), request.getLimit()), Wrappers.<LogStockOpt>lambdaQuery()
                .like(StringUtils.isNotBlank(request.getOpeningStockSn()), LogStockOpt::getOpeningStockSn, request.getOpeningStockSn())
                .like(StringUtils.isNotBlank(request.getClosingStockSn()), LogStockOpt::getClosingStockSn, request.getClosingStockSn())
                .between(StringUtils.isNotBlank(request.getStartTime()) && StringUtils.isNotBlank(request.getEndTime()), LogStockOpt::getUpdatedTime, request.getStartTime(), request.getEndTime())
                .like(StringUtils.isNotBlank(request.getUpdatedBy()), LogStockOpt::getUpdatedBy, request.getUpdatedBy())
                .orderByDesc(LogStockOpt::getUpdatedTime));

        if (0L == page.getTotal()) {
            return PageResult.<AccountQueryResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .build();
        }

        List<StockExt> stockExtList = stockService.selectByStockIdList(page.getRecords().stream().map(LogStockOpt::getStockId).collect(Collectors.toList()));

        return PageResult.<LogStockOptListResult>builder()
                .result(page.getRecords()
                        .stream()
                        .map(r -> {

                            LogStockOptListResult result = LogStockOptConverter.INSTANCE.convertList(r);
                            StockExt stockExt = stockExtList.stream().filter(t -> t.getStockId().equals(r.getStockId())).findAny().orElse(null);
                            if (ObjectUtils.isNotEmpty(stockExt)) {
                                result.setBrandName(stockExt.getBrandName());
                                result.setSeriesName(stockExt.getSeriesName());
                                result.setModel(stockExt.getModel());
                            }
                            return result;
                        })
                        .collect(Collectors.toList())
                )
                .totalCount(page.getTotal())
                .totalPage(page.getPages())
                .build();
    }
}
