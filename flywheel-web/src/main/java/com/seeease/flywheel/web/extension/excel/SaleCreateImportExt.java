package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.sale.ISaleOrderFacade;
import com.seeease.flywheel.sale.request.SaleStockQueryImportRequest;
import com.seeease.flywheel.sale.result.SaleStockQueryImportResult;
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
 * 销售导入
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.SALE_CREATE)
public class SaleCreateImportExt implements ImportExtPtl<SaleStockQueryImportRequest, SaleStockQueryImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private ISaleOrderFacade saleOrderFacade;

    @Override
    public Class<SaleStockQueryImportRequest> getRequestClass() {
        return SaleStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<SaleStockQueryImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(SaleStockQueryImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(SaleStockQueryImportRequest.ImportDto::getStockSn))
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
    public ImportResult<SaleStockQueryImportResult> handle(ImportCmd<SaleStockQueryImportRequest> cmd) {
        return saleOrderFacade.stockQueryImport(cmd.getRequest());
    }
}
