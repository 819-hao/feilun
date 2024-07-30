package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockPromotionFacade;
import com.seeease.flywheel.goods.request.StockPromotionImportRequest;
import com.seeease.flywheel.goods.result.StockPromotionImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import com.seeease.springframework.utils.BigDecimalUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 销售寄售结算导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STOCK_PROMOTION)
public class StockPromotionImportExt implements ImportExtPtl<StockPromotionImportRequest, StockPromotionImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IStockPromotionFacade facade;

    @Override
    public Class<StockPromotionImportRequest> getRequestClass() {
        return StockPromotionImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StockPromotionImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        cmd.getRequest().getDataList().forEach(t -> {
            if (StringUtils.isEmpty(t.getStockSn())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
            }
            if (Objects.isNull(t.getPromotionPrice()) || BigDecimalUtil.ltZero(t.getPromotionPrice())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_PROMOTION_PRICE_NON_NULL);
            }
            if (Objects.isNull(t.getConsignmentRatio()) || BigDecimalUtil.leZero(t.getConsignmentRatio())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_PROMOTION_RATIO_PRICE_NON_NULL);
            }
        });

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(StockPromotionImportRequest.ImportDto::getStockSn))
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
    public ImportResult<StockPromotionImportResult> handle(ImportCmd<StockPromotionImportRequest> cmd) {
        return facade.stockQueryImport(cmd.getRequest());
    }
}
