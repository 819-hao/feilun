package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockGuaranteeCardManageFacade;
import com.seeease.flywheel.pricing.request.StockGuaranteeCardManageImportRequest;
import com.seeease.flywheel.pricing.result.StockGuaranteeCardManageImportResult;
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

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STOCK_GUARANTEE_CARD_MANAGE)
public class StockGuaranteeCardManageImportExt implements ImportExtPtl<StockGuaranteeCardManageImportRequest, StockGuaranteeCardManageImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStockGuaranteeCardManageFacade manageFacade;

    @Override
    public Class<StockGuaranteeCardManageImportRequest> getRequestClass() {
        return StockGuaranteeCardManageImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StockGuaranteeCardManageImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");
        cmd.getRequest().getDataList().forEach(t -> {
            if (StringUtils.isBlank(t.getStockSn())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
            }
        });
    }

    @Override
    public ImportResult<StockGuaranteeCardManageImportResult> handle(ImportCmd<StockGuaranteeCardManageImportRequest> cmd) {
        return manageFacade.importHandle(cmd.getRequest());
    }
}
