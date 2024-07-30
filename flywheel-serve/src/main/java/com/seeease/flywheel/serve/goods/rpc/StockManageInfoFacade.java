package com.seeease.flywheel.serve.goods.rpc;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.IStockManageInfoFacade;
import com.seeease.flywheel.goods.request.StockManageInfoImportRequest;
import com.seeease.flywheel.goods.request.StockManageInfoListRequest;
import com.seeease.flywheel.goods.result.StockManageInfoImportResult;
import com.seeease.flywheel.goods.result.StockManageInfoListResult;
import com.seeease.flywheel.serve.goods.convert.StockManageInfoConverter;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockManageInfo;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.service.StockManageInfoService;
import com.seeease.flywheel.serve.goods.service.StockService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/8/8
 */
@DubboService(version = "1.0.0")
public class StockManageInfoFacade implements IStockManageInfoFacade {
    @Resource
    private StockManageInfoService stockManageInfoService;
    @Resource
    private StockService stockService;

    @Override
    public PageResult<StockManageInfoListResult> list(StockManageInfoListRequest request) {

        Page<StockManageInfo> res = stockManageInfoService.page(Page.of(request.getPage(), request.getLimit()), Wrappers.<StockManageInfo>lambdaUpdate()
                .eq(StringUtils.isNoneBlank(request.getStockSn()), StockManageInfo::getStockSn, request.getStockSn())
                .eq(StringUtils.isNoneBlank(request.getBoxNumber()), StockManageInfo::getBoxNumber, request.getBoxNumber())
                .eq(StringUtils.isNoneBlank(request.getStorageRegion()), StockManageInfo::getStorageRegion, request.getStorageRegion())
                .eq(StringUtils.isNoneBlank(request.getStorageSubsegment()), StockManageInfo::getStorageSubsegment, request.getStorageSubsegment()));

        return PageResult.<StockManageInfoListResult>builder()
                .totalCount(res.getTotal())
                .totalPage(res.getPages())
                .result(res.getRecords().stream()
                        .map(StockManageInfoConverter.INSTANCE::convertListResult)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public ImportResult<StockManageInfoImportResult> stockManageInfoImport(StockManageInfoImportRequest request) {
        List<String> stockSnList = request.getDataList().stream()
                .map(StockManageInfoImportRequest.ImportDto::getStockSn)
                .collect(Collectors.toList());

        Map<String, Stock> stockMap = Lists.partition(stockSnList, 500)
                .stream()
                .map(snList -> stockService.list(Wrappers.<Stock>lambdaQuery()
                        .in(Stock::getSn, snList)
                        .isNull(Stock::getTemp)
                        .in(Stock::getStockStatus, Lists.newArrayList(StockStatusEnum.MARKETABLE
                                , StockStatusEnum.PURCHASE_IN_TRANSIT
                                , StockStatusEnum.WAIT_RECEIVED
                                , StockStatusEnum.WAIT_PRICING
                                , StockStatusEnum.ALLOCATE_IN_TRANSIT
                                , StockStatusEnum.EXCEPTION
                                , StockStatusEnum.EXCEPTION_IN
                        ))))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Stock::getSn, Function.identity()));

        //校验不可改的数据
        Collection<String> errorStock = CollectionUtils.subtract(stockSnList, stockMap.keySet().stream().collect(Collectors.toList()));

        //转换数据
        List<StockManageInfo> data = request.getDataList()
                .stream()
                .filter(t -> stockMap.containsKey(t.getStockSn()))
                .map(t -> {
                    Stock stock = stockMap.get(t.getStockSn());
                    StockManageInfo info = StockManageInfoConverter.INSTANCE.convert(t);
                    info.setStockId(stock.getId());
                    return info;
                }).collect(Collectors.toList());

        //新增或更新
        data.forEach(info -> stockManageInfoService.saveOrUpdate(info, Wrappers.<StockManageInfo>lambdaUpdate()
                .eq(StockManageInfo::getStockId, info.getStockId())));


        return ImportResult.<StockManageInfoImportResult>builder()
                .successList(data.stream()
                        .map(StockManageInfoConverter.INSTANCE::convertImportResult)
                        .collect(Collectors.toList()))
                .errList(errorStock.stream().collect(Collectors.toList()))
                .build();
    }
}
