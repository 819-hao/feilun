package com.seeease.flywheel.serve.pricing.rpc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.pricing.IApplyPricingFacade;
import com.seeease.flywheel.pricing.request.ApplyPricingAuditorRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingCreateRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingEditRequest;
import com.seeease.flywheel.pricing.request.ApplyPricingListRequest;
import com.seeease.flywheel.pricing.result.ApplyPricingAuditorResult;
import com.seeease.flywheel.pricing.result.ApplyPricingCreateResult;
import com.seeease.flywheel.pricing.result.ApplyPricingEditResult;
import com.seeease.flywheel.pricing.result.ApplyPricingListResult;
import com.seeease.flywheel.serve.base.OperationExceptionCode;
import com.seeease.flywheel.serve.base.SerialNoGenerator;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.WatchDataFusion;
import com.seeease.flywheel.serve.goods.service.GoodsWatchService;
import com.seeease.flywheel.serve.goods.service.StockService;
import com.seeease.flywheel.serve.maindata.entity.StoreManagementInfo;
import com.seeease.flywheel.serve.maindata.service.StoreManagementService;
import com.seeease.flywheel.serve.pricing.convert.ApplyPricingConvert;
import com.seeease.flywheel.serve.pricing.entity.BillApplyPricing;
import com.seeease.flywheel.serve.pricing.enums.ApplyPricingStateEnum;
import com.seeease.flywheel.serve.pricing.service.BillApplyPricingService;
import com.seeease.springframework.context.UserContext;
import com.seeease.springframework.exception.e.OperationRejectedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调价申请服务
 *
 * @author Tiro
 * @date 2024/2/23
 */
@Slf4j
@DubboService(version = "1.0.0")
public class ApplyPricingFacade implements IApplyPricingFacade {
    @Resource
    private StockService stockService;

    @Resource
    private GoodsWatchService goodsWatchService;

    @Resource
    private BillApplyPricingService applyPricingService;

    @Resource
    private StoreManagementService storeManagementService;

    @Override
    public PageResult<ApplyPricingListResult> list(ApplyPricingListRequest request) {

        List<Integer> stockIds = Optional.ofNullable(request.getStockSn())
                .filter(StringUtils::isNotBlank)
                .map(stockService::findByStockSn)
                .map(t -> t.stream().map(Stock::getId).collect(Collectors.toList()))
                .orElse(null);

        LambdaQueryWrapper<BillApplyPricing> wrappers = Wrappers.<BillApplyPricing>lambdaQuery()
                .between(StringUtils.isNotEmpty(request.getStartTime()) && StringUtils.isNotEmpty(request.getEndTime()),
                        BillApplyPricing::getCreatedTime, request.getStartTime(), request.getEndTime())
                .in(CollectionUtils.isNotEmpty(stockIds), BillApplyPricing::getStockId, stockIds)
                .eq(Objects.nonNull(request.getApplyStatus()), BillApplyPricing::getApplyStatus, request.getApplyStatus())
                .eq(Objects.nonNull(request.getApplyShopId()), BillApplyPricing::getApplyShopId, request.getApplyShopId())
                .like(StringUtils.isNotBlank(request.getCreatedBy()), BillApplyPricing::getCreatedBy, request.getCreatedBy());

        if (Objects.nonNull(request.getApplyStatus())
                && (ApplyPricingStateEnum.PASS.getValue().intValue() == request.getApplyStatus()
                || ApplyPricingStateEnum.REJECTION.getValue().intValue() == request.getApplyStatus())) {
            wrappers.orderByDesc(BillApplyPricing::getApprovedTime);
        }

        Page<BillApplyPricing> pageResult = applyPricingService.page(Page.of(request.getPage(), request.getLimit()), wrappers);

        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return PageResult.<ApplyPricingListResult>builder()
                    .result(Collections.EMPTY_LIST)
                    .build();
        }


        List<Integer> stockIdList = pageResult.getRecords()
                .stream()
                .map(BillApplyPricing::getStockId)
                .collect(Collectors.toList());

        Map<Integer, WatchDataFusion> goodsMap = goodsWatchService.getWatchDataFusionListByStockIds(stockIdList)
                .stream()
                .collect(Collectors.toMap(WatchDataFusion::getStockId, Function.identity()));

        Map<Integer, Stock> stockMap = stockService.listByIds(stockIdList)
                .stream()
                .collect(Collectors.toMap(Stock::getId, Function.identity()));

        Map<Integer, String> tagMap = storeManagementService.selectInfoByIds(pageResult.getRecords()
                        .stream()
                        .map(BillApplyPricing::getApplyShopId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(StoreManagementInfo::getId, StoreManagementInfo::getName));


        return PageResult.<ApplyPricingListResult>builder()
                .result(pageResult.getRecords()
                        .stream()
                        .map(t -> {
                            ApplyPricingListResult r = ApplyPricingConvert.INSTANCE.convertListResult(t);

                            Stock stock = stockMap.get(t.getStockId());
                            r.setTotalPrice(stock.getTotalPrice());
                            r.setTocPrice(stock.getTocPrice());
                            r.setTobPrice(stock.getTobPrice());
                            r.setTagPrice(stock.getTagPrice());
                            r.setStockSn(stock.getSn());
                            r.setFiness(stock.getFiness());
                            r.setAttachment(stock.getAttachment());
                            r.setTotalStorageAge(stock.getTotalStorageAge());
                            r.setStockSrc(stock.getStockSrc());

                            WatchDataFusion goods = goodsMap.get(t.getStockId());
                            if (Objects.nonNull(goods)) {
                                r.setImage(goods.getImage());
                                r.setBrandName(goods.getBrandName());
                                r.setSeriesName(goods.getSeriesName());
                                r.setModel(goods.getModel());
                            }

                            r.setApplyShopName(tagMap.get(t.getApplyShopId()));
                            return r;
                        })
                        .collect(Collectors.toList())
                )
                .totalCount(pageResult.getTotal())
                .totalPage(pageResult.getPages())
                .build();
    }

    @Override
    public ApplyPricingCreateResult create(ApplyPricingCreateRequest request) {
        BillApplyPricing billApplyPricing = ApplyPricingConvert.INSTANCE.convert(request);
        billApplyPricing.setSerialNo(SerialNoGenerator.generateApplyPricingSerialNo());
        billApplyPricing.setApplyShopId(UserContext.getUser().getStore().getId());
        billApplyPricing.setApplyStatus(ApplyPricingStateEnum.CREATE);
        applyPricingService.save(billApplyPricing);
        return ApplyPricingConvert.INSTANCE.convertCreateResult(billApplyPricing);
    }

    @Override
    public ApplyPricingEditResult edit(ApplyPricingEditRequest request) {
        BillApplyPricing billApplyPricing = ApplyPricingConvert.INSTANCE.convert(request);
        applyPricingService.updateById(billApplyPricing);
        return ApplyPricingConvert.INSTANCE.convertEditResult(billApplyPricing);
    }

    @Override
    public ApplyPricingAuditorResult auditor(ApplyPricingAuditorRequest request) {
        request.setAuditor(UserContext.getUser().getUserName());
        ApplyPricingAuditorResult result = applyPricingService.auditor(request);
        if (result.isAutoRefuse()) {
            throw new OperationRejectedException(OperationExceptionCode.APPLY_PRICING_SYSTEM_REJECTION);
        }
        return result;
    }
}
