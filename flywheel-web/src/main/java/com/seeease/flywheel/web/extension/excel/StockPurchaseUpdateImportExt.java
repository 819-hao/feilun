package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.request.StockPurchaseUpdateImportRequest;
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

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 采购价修改导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STOCK_PURCHASE_UPDATE)
public class StockPurchaseUpdateImportExt implements ImportExtPtl<StockPurchaseUpdateImportRequest, StockPromotionImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade facade;

    @Override
    public Class<StockPurchaseUpdateImportRequest> getRequestClass() {
        return StockPurchaseUpdateImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StockPurchaseUpdateImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(StockPurchaseUpdateImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(StockPurchaseUpdateImportRequest.ImportDto::getClinchPrice)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_PROMOTION_PRICE_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(StockPurchaseUpdateImportRequest.ImportDto::getPurchasePrice)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_PROMOTION_PRICE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(StockPurchaseUpdateImportRequest.ImportDto::getStockSn))
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
    public ImportResult<StockPromotionImportResult> handle(ImportCmd<StockPurchaseUpdateImportRequest> cmd) {
        return facade.stockPurchaseUpdate(cmd.getRequest());
    }
}
