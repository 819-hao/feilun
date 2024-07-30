package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockPromotionFacade;
import com.seeease.flywheel.goods.request.StockPromotionImportRequest;
import com.seeease.flywheel.goods.request.StockPromotionTakeDownImportRequest;
import com.seeease.flywheel.goods.result.StockPromotionImportResult;
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

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 活动商品列表下架导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STOCK_PROMOTION_TAKE_DOWN)
public class StockPromotionTakeDownImportExt implements ImportExtPtl<StockPromotionTakeDownImportRequest, Void> {
    @DubboReference(check = false, version = "1.0.0")
    private IStockPromotionFacade facade;

    @Override
    public Class<StockPromotionTakeDownImportRequest> getRequestClass() {
        return StockPromotionTakeDownImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StockPromotionTakeDownImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(StockPromotionTakeDownImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(StockPromotionTakeDownImportRequest.ImportDto::getStockSn))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(repeatStockSn)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REPEAT, repeatStockSn);
        }
    }

    @Override
    public ImportResult<Void> handle(ImportCmd<StockPromotionTakeDownImportRequest> cmd) {
         facade.stockQueryTakeDownImport(cmd.getRequest());
         return ImportResult.<Void>builder().build();
    }
}
