package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.financial.IFinancialStatementFacade;
import com.seeease.flywheel.financial.request.FinancialStatementImportRequest;
import com.seeease.flywheel.financial.result.FinancialStatementImportResult;
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

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 财务流水导入
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.FINANCIAL_STATEMENT)
public class FinancialStatementImportExt implements ImportExtPtl<FinancialStatementImportRequest, FinancialStatementImportResult> {
    @DubboReference(check = false, version = "1.0.0")
    private IFinancialStatementFacade facade;

    @Override
    public Class<FinancialStatementImportRequest> getRequestClass() {
        return FinancialStatementImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<FinancialStatementImportRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");

        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getSerialNo)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_SERIAL_NO_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getShopName)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_SHOP_NAME_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getSubjectName)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_SUBJECT_NAME_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getPayer)
                .anyMatch(StringUtils::isEmpty)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_PAYER_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getCollectionTime)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_COLLECTION_TIME_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getWaitAuditPrice)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_PRICE_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getProcedureFee)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_PRICE_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getReceivableAmount)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_PRICE_NON_NULL);
        }
        if (cmd.getRequest().getDataList().stream().map(FinancialStatementImportRequest.ImportDto::getFundsReceived)
                .anyMatch(Objects::isNull)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.FINANCIAL_STATEMENT_PRICE_NON_NULL);
        }


        String repeatSerialNo = cmd.getRequest().getDataList().stream()
                .collect(Collectors.groupingBy(FinancialStatementImportRequest.ImportDto::getSerialNo))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(repeatSerialNo)) {
            throw new OperationRejectedException(OperationExceptionCodeEnum.STATEMENT_SERIAL_NO_REPEAT, repeatSerialNo);
        }
    }

    @Override
    public ImportResult<FinancialStatementImportResult> handle(ImportCmd<FinancialStatementImportRequest> cmd) {
        return facade.financialStatementImport(cmd.getRequest());
    }
}
