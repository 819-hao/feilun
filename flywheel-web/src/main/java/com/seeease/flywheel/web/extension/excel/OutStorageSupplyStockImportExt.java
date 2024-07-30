package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.alibaba.fastjson.JSONObject;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.StockQueryRequest;
import com.seeease.flywheel.storework.IStoreWorkFacade;
import com.seeease.flywheel.storework.IStoreWorkQueryFacade;
import com.seeease.flywheel.storework.request.OutStorageSupplyStockImportRequest;
import com.seeease.flywheel.storework.request.StoreWorkOutStorageSupplyStockRequest;
import com.seeease.flywheel.storework.result.OutStorageSupplyStockImportResult;
import com.seeease.flywheel.storework.result.StoreWorkListResult;
import com.seeease.flywheel.storework.result.StoreWorkOutStorageSupplyStockResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 出库保存表身号导入
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Slf4j
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.OUT_STORAGE_SUPPLY_STOCK)
public class OutStorageSupplyStockImportExt implements ImportExtPtl<OutStorageSupplyStockImportRequest, OutStorageSupplyStockImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkQueryFacade storeWorkQueryFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IStoreWorkFacade storeWorkFacade;
    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade stockFacade;


    @Override
    public Class<OutStorageSupplyStockImportRequest> getRequestClass() {
        return OutStorageSupplyStockImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<OutStorageSupplyStockImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.notNull(cmd.getRequest().getUseScenario(), "场景不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");
        Assert.isTrue(StringUtils.isNotEmpty(cmd.getRequest().getOriginSerialNo()), "单号不能为空");

        if (cmd.getRequest().getDataList().stream().map(OutStorageSupplyStockImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(OutStorageSupplyStockImportRequest.ImportDto::getStockSn))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(repeatStockSn)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REPEAT, repeatStockSn);
        }
    }

    @Override
    public ImportResult<OutStorageSupplyStockImportResult> handle(ImportCmd<OutStorageSupplyStockImportRequest> cmd) {
        List<String> originSerialNoList = Arrays.stream(cmd.getRequest().getOriginSerialNo().split(","))
                .map(StringUtils::trim)
                .collect(Collectors.toList());
        Map<String, List<StoreWorkListResult>> workListResultMap = storeWorkQueryFacade.listByOriginSerialNo(originSerialNoList)
                .stream()
                .collect(Collectors.groupingBy(StoreWorkListResult::getOriginSerialNo));
        if (MapUtils.isEmpty(workListResultMap)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        //无效单据
        List<String> errWork = originSerialNoList
                .stream()
                .filter(t -> !workListResultMap.containsKey(t))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(errWork)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_WORK_ERROR, JSONObject.toJSONString(errWork));
        }

        //查商品
        List<String> stockSnList = cmd.getRequest().getDataList()
                .stream()
                .map(OutStorageSupplyStockImportRequest.ImportDto::getStockSn)
                .collect(Collectors.toList());

        List<StockBaseInfo> stockBaseInfos = stockFacade.queryByStockSn(StockQueryRequest.builder()
                .isSaleable(true)
                .stockSnList(stockSnList)
                .build());

        Map<Integer/*goodsId*/, List<StockBaseInfo>> stockMap = stockBaseInfos
                .stream().collect(Collectors.groupingBy(StockBaseInfo::getGoodsId));
        if (MapUtils.isEmpty(stockMap)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_STOCK_SN);
        }

        //无效表身号
        List<String> stock = stockBaseInfos.stream().map(StockBaseInfo::getStockSn).collect(Collectors.toList());
        List<String> errStock = stockSnList.stream()
                .filter(t -> !stock.contains(t))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(errStock)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_ERROR, JSONObject.toJSONString(errStock));
        }

        List<String> useStockSnList = new ArrayList<>();

        //单个循环处理
        List<Integer> workIds = originSerialNoList.stream().map(originSerialNo -> {
                    try {
                        return handleSingle(originSerialNo,
                                cmd.getRequest().getUseScenario(),
                                useStockSnList,
                                workListResultMap,
                                stockMap);
                    } catch (Exception e) {
                        log.error("导入保存表身号异常:{}-{}", originSerialNo, e.getMessage(), e);
                        return null;
                    }
                }).filter(Objects::nonNull)
                .map(StoreWorkOutStorageSupplyStockResult::getWorkIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return ImportResult.<OutStorageSupplyStockImportResult>builder()
                .successList(workIds
                        .stream()
                        .map(id -> OutStorageSupplyStockImportResult.builder()
                                .workId(id)
                                .build())
                        .collect(Collectors.toList()))
                .errList(CollectionUtils.subtract(stockSnList, useStockSnList)
                        .stream()
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * @param originSerialNo
     * @param useScenario
     * @param useStockSnList    已经使用的表身号
     * @param workListResultMap
     * @param stockMap
     */
    private StoreWorkOutStorageSupplyStockResult handleSingle(String originSerialNo,
                                                              StoreWorkOutStorageSupplyStockRequest.SupplyScenario useScenario,
                                                              List<String> useStockSnList,
                                                              Map<String/*originSerialNo*/, List<StoreWorkListResult>> workListResultMap,
                                                              Map<Integer/*goodsId*/, List<StockBaseInfo>> stockMap) {
        List<StoreWorkListResult> outStorageDetails = workListResultMap.get(originSerialNo);
        //没有需要补充表身号的数据
        if (CollectionUtils.isEmpty(outStorageDetails)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
        }
        //构建请求参数
        List<String> thisUseStockSnList = new ArrayList<>();
        List<StoreWorkOutStorageSupplyStockRequest.OutStorageSupplyStockDto> lineList = outStorageDetails.stream()
                .filter(t -> Objects.isNull(t.getStockId()))
                .filter(t -> stockMap.containsKey(t.getGoodsId()))
                .map(t -> {
                    StockBaseInfo stock = stockMap.get(t.getGoodsId())
                            .stream()
                            .filter(i -> !useStockSnList.contains(i.getStockSn())
                                    && !thisUseStockSnList.contains(i.getStockSn()))
                            .findFirst()
                            .orElse(null);
                    if (Objects.isNull(stock)) {
                        return null;
                    }
                    //保存已使用表身号
                    thisUseStockSnList.add(stock.getStockSn());

                    return StoreWorkOutStorageSupplyStockRequest.OutStorageSupplyStockDto
                            .builder()
                            .id(t.getId())
                            .stockSn(stock.getStockSn())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //保存表身号
        StoreWorkOutStorageSupplyStockResult res = storeWorkFacade.outStorageSupplyStock(StoreWorkOutStorageSupplyStockRequest
                .builder()
                .originSerialNo(originSerialNo)
                .scenario(useScenario)
                .lineList(lineList)
                .build());
        //成功之后记录为已使用
        useStockSnList.addAll(thisUseStockSnList);

        return res;
    }


}
