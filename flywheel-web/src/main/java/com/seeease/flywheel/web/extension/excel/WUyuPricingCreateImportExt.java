package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.WuyuPricingImportRequest;
import com.seeease.flywheel.sale.ISaleReturnOrderFacade;
import com.seeease.flywheel.sale.request.SaleReturnStockQueryImportRequest;
import com.seeease.flywheel.sale.result.SaleReturnStockQueryImportResult;
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
 * 销售退货导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.WUYU_PRICING)
public class WUyuPricingCreateImportExt implements ImportExtPtl<WuyuPricingImportRequest, Void> {
    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade facade;

    @Override
    public Class<WuyuPricingImportRequest> getRequestClass() {
        return WuyuPricingImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<WuyuPricingImportRequest> cmd) {
    }

    @Override
    public ImportResult<Void> handle(ImportCmd<WuyuPricingImportRequest> cmd) {
        facade.wuyuPricingImport(cmd.getRequest());
        return new ImportResult<>();
    }
}
