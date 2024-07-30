package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockFacade;
import com.seeease.flywheel.goods.request.SettleStockQueryImportRequest;
import com.seeease.flywheel.goods.result.SettleStockQueryImportResult;
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

import java.util.stream.Collectors;

/**
 * 同行寄售结算导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.PURCHASE_BATCH_SETTLE)
public class PurchaseBatchSettleImportExt implements ImportExtPtl<SettleStockQueryImportRequest, SettleStockQueryImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IStockFacade facade;

    @Override
    public Class<SettleStockQueryImportRequest> getRequestClass() {
        return SettleStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<SettleStockQueryImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(SettleStockQueryImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(SettleStockQueryImportRequest.ImportDto::getStockSn))
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
    public ImportResult<SettleStockQueryImportResult> handle(ImportCmd<SettleStockQueryImportRequest> cmd) {
        return facade.settleStockQueryImport(cmd.getRequest());
    }
}
