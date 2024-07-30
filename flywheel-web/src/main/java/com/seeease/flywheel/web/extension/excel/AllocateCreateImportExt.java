package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.allocate.IAllocateFacade;
import com.seeease.flywheel.allocate.request.AllocateStockQueryImportRequest;
import com.seeease.flywheel.allocate.result.AllocateStockQueryImportResult;
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
 * 调拨导入
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.ALLOCATE_CREATE)
public class AllocateCreateImportExt implements ImportExtPtl<AllocateStockQueryImportRequest, AllocateStockQueryImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IAllocateFacade allocateFacade;

    @Override
    public Class<AllocateStockQueryImportRequest> getRequestClass() {
        return AllocateStockQueryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<AllocateStockQueryImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(AllocateStockQueryImportRequest.ImportDto::getStockSn)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STOCK_SN_REQUIRE_NON_NULL);
        }

        String repeatStockSn = cmd.getRequest().getDataList().stream().collect(Collectors.groupingBy(AllocateStockQueryImportRequest.ImportDto::getStockSn))
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
    public ImportResult<AllocateStockQueryImportResult> handle(ImportCmd<AllocateStockQueryImportRequest> cmd) {
        return allocateFacade.stockQueryImport(cmd.getRequest());
    }


}
