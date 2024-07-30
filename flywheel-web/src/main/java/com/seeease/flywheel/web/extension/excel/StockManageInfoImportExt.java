package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.goods.IStockManageInfoFacade;
import com.seeease.flywheel.goods.request.StockManageInfoImportRequest;
import com.seeease.flywheel.goods.result.StockManageInfoImportResult;
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
 * 库存盒号管理导入
 *
 * @author Tiro
 * @date 2023/8/8
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STOCK_MANAGE_INFO)
public class StockManageInfoImportExt implements ImportExtPtl<StockManageInfoImportRequest, StockManageInfoImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IStockManageInfoFacade stockManageInfoFacade;

    @Override
    public Class<StockManageInfoImportRequest> getRequestClass() {
        return StockManageInfoImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StockManageInfoImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(StockManageInfoImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        if (cmd.getRequest().getDataList().stream().map(StockManageInfoImportRequest.ImportDto::getBoxNumber)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.BOX_NUMBER_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(StockManageInfoImportRequest.ImportDto::getStockSn))
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
    public ImportResult<StockManageInfoImportResult> handle(ImportCmd<StockManageInfoImportRequest> cmd) {
        return stockManageInfoFacade.stockManageInfoImport(cmd.getRequest());
    }
}
