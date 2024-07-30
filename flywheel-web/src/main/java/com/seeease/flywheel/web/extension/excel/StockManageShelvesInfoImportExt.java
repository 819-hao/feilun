package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockManageShelvesInfoFacade;
import com.seeease.flywheel.goods.request.StockManageShelvesInfoImportRequest;
import com.seeease.flywheel.goods.result.StockManageShelvesInfoImportResult;
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
 * 库存盒号管理导入
 *
 * @author Tiro
 * @date 2023/8/8
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STOCK_MANAGE_SHELVES_INFO)
public class StockManageShelvesInfoImportExt implements ImportExtPtl<StockManageShelvesInfoImportRequest, StockManageShelvesInfoImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStockManageShelvesInfoFacade stockManageShelvesInfoFacade;

    @Override
    public Class<StockManageShelvesInfoImportRequest> getRequestClass() {
        return StockManageShelvesInfoImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StockManageShelvesInfoImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(StockManageShelvesInfoImportRequest.ImportDto::getModel)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.MODEL_REQUIRE_NON_NULL);
        }

        if (cmd.getRequest().getDataList().stream().map(StockManageShelvesInfoImportRequest.ImportDto::getBrandName)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.BRAND_REQUIRE_NON_NULL);
        }

        if (cmd.getRequest().getDataList().stream().map(StockManageShelvesInfoImportRequest.ImportDto::getShelvesSimplifiedCode)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.SHELVES_SIMPLIFIED_CODE_REQUIRE_NON_NULL);
        }
    }

    @Override
    public ImportResult<StockManageShelvesInfoImportResult> handle(ImportCmd<StockManageShelvesInfoImportRequest> cmd) {
        return stockManageShelvesInfoFacade.infoImport(cmd.getRequest());
    }
}
