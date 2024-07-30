package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IGoodsExtFacade;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.purchase.request.PurchasePlanImportRequest;
import com.seeease.flywheel.purchase.result.PurchasePlanImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购计划导入
 *
 * @author Tiro
 * @date 2023/9/12
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.PURCHASE_PLAN)
public class PurchasePlanCreateImportExt implements ImportExtPtl<PurchasePlanImportRequest, PurchasePlanImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IGoodsExtFacade goodsExtFacade;

    @Override
    public Class<PurchasePlanImportRequest> getRequestClass() {
        return PurchasePlanImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<PurchasePlanImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");
        for (PurchasePlanImportRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            /**
             * 型号
             */
            if (StringUtils.isBlank(importDto.getModel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
            }
            /**
             * 成色
             */
            if (StringUtils.isBlank(importDto.getBrandName())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.BRAND_REQUIRE_NON_NULL);
            }
        }
        if (cmd.getRequest()
                .getDataList()
                .stream()
                .map(t -> t.getBrandName() + t.getModel())
                .distinct()
                .count() != cmd.getRequest()
                .getDataList().size()) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.DATA_REPEAT);
        }
    }

    @Override
    public ImportResult<PurchasePlanImportResult> handle(ImportCmd<PurchasePlanImportRequest> cmd) {
        Map<String/*model*/, List<GoodsBaseInfo>> goodsMap = goodsExtFacade.listGoods(GoodsListRequest.builder()
                        .modelList(cmd.getRequest().getDataList()
                                .stream()
                                .map(PurchasePlanImportRequest.ImportDto::getModel)
                                .collect(Collectors.toList()))
                        .build())
                .getResult()
                .stream()
                .collect(Collectors.groupingBy(GoodsBaseInfo::getModel));

        List<PurchasePlanImportResult> successList = new ArrayList<>();
        List<String> errModel = new ArrayList<>();

        cmd.getRequest().getDataList()
                .forEach(t -> {
                    GoodsBaseInfo goods = Optional.ofNullable(goodsMap.get(t.getModel()))
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(s -> s.getBrandName().equals(t.getBrandName()))
                            .findFirst()
                            .orElse(null);
                    if (Objects.nonNull(goods)) {
                        successList.add(PurchasePlanImportResult.builder()
                                .goodsId(goods.getGoodsId())
                                .image(goods.getImage())
                                .brandName(goods.getBrandName())
                                .seriesName(goods.getSeriesName())
                                .model(goods.getModel())
                                .pricePub(goods.getPricePub())
                                .currentPrice(goods.getCurrentPrice())
                                .twoZeroFullPrice(goods.getTwoZeroFullPrice())
                                .twoTwoFullPrice(goods.getTwoTwoFullPrice())
                                .planNumber(t.getPlanNumber())
                                .build());
                    } else {
                        errModel.add(t.getModel());
                    }
                });

        return ImportResult.<PurchasePlanImportResult>builder()
                .successList(successList)
                .errList(errModel)
                .build();
    }
}
