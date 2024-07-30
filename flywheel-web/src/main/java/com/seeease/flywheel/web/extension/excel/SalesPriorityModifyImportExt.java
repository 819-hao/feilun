package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.pricing.IPricingFacade;
import com.seeease.flywheel.pricing.request.SalesPriorityModifyImportRequest;
import com.seeease.flywheel.pricing.result.SalesPriorityModifyImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author Tiro
 * @date 2023/11/8
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.SALES_PRIORITY_MODIFY)
public class SalesPriorityModifyImportExt implements ImportExtPtl<SalesPriorityModifyImportRequest, SalesPriorityModifyImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPricingFacade pricingFacade;

    @Override
    public Class<SalesPriorityModifyImportRequest> getRequestClass() {
        return SalesPriorityModifyImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<SalesPriorityModifyImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        cmd.getRequest().getDataList().forEach(t -> {
            /**
             * 型号
             */
            if (StringUtils.isBlank(t.getBrandName())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.BRAND_REQUIRE_NON_NULL);
            }
            /**
             * 型号
             */
            if (StringUtils.isBlank(t.getModel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
            }
            /**
             * 销售优先等级/分级
             */
            if (StringUtils.isBlank(t.getSalesPriority()) && StringUtils.isBlank(t.getGoodsLevel())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.INVALID_OPERATION);
            }
        });

        if (cmd.getRequest().getDataList().stream().map(SalesPriorityModifyImportRequest.ImportDto::getBrandName)
                .distinct()
                .count() > NumberUtils.INTEGER_ONE) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.ONLY_SUPPORTED_SINGLE_BRAND);
        }
    }

    @Override
    public ImportResult<SalesPriorityModifyImportResult> handle(ImportCmd<SalesPriorityModifyImportRequest> cmd) {
        return pricingFacade.salesPriorityModifyImport(cmd.getRequest());
    }
}
