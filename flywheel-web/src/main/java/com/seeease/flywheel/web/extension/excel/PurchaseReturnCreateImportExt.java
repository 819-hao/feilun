package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.purchase.IPurchaseReturnFacade;
import com.seeease.flywheel.purchase.request.PurchaseReturnStockQueryImportRequest;
import com.seeease.flywheel.purchase.result.PurchaseReturnStockQueryImportResult;
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
 * @Author Mr. Du
 * @Description 采购导入
 * @Date create in 2023/3/31 10:51
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.PURCHASE_RETURN_CREATE)
public class PurchaseReturnCreateImportExt implements ImportExtPtl<PurchaseReturnStockQueryImportRequest, PurchaseReturnStockQueryImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IPurchaseReturnFacade purchaseReturnFacade;

    @Override
    public Class<PurchaseReturnStockQueryImportRequest> getRequestClass() {
        return PurchaseReturnStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<PurchaseReturnStockQueryImportRequest> cmd) {

        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        for (PurchaseReturnStockQueryImportRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            /**
             * 表身号
             */
            if (StringUtils.isBlank(importDto.getStockSn())) {
                throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
            }
            //去除前后空格
            importDto.setStockSn(importDto.getStockSn().trim());
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(PurchaseReturnStockQueryImportRequest.ImportDto::getStockSn))
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
    public ImportResult<PurchaseReturnStockQueryImportResult> handle(ImportCmd<PurchaseReturnStockQueryImportRequest> cmd) {
        return purchaseReturnFacade.stockQueryImport(cmd.getRequest());
    }
}
