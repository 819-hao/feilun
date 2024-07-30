package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.account.IAccountFacade;
import com.seeease.flywheel.account.request.AccountImportByManpowerQueryRequest;
import com.seeease.flywheel.account.result.AccountQueryImportResult;
import com.seeease.flywheel.web.common.context.OperationExceptionCodeEnum;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import com.seeease.springframework.exception.e.OperationRejectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @Author Mr. Du
 * @Description 人力
 * @Date create in 2023/7/18 15:38
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.MANPOWER_CREATE)
public class KeepAccountByManpowerImportExt implements ImportExtPtl<AccountImportByManpowerQueryRequest, AccountQueryImportResult> {

    @DubboReference(check = false, version = "1.0.0")
    private IAccountFacade accountFacade;


    @Override
    public Class<AccountImportByManpowerQueryRequest> getRequestClass() {
        return AccountImportByManpowerQueryRequest.class;
    }

    @Override
    public void validate(ImportCmd<AccountImportByManpowerQueryRequest> cmd) {
        Assert.notNull(cmd.getRequest(), "数据不能为空");
        Assert.isTrue(CollectionUtils.isNotEmpty(cmd.getRequest().getDataList()), "数据不能为空");
        for (AccountImportByManpowerQueryRequest.ImportDto importDto : cmd.getRequest().getDataList()) {
            Optional.ofNullable(importDto.getAccountGroup()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL));
            Optional.ofNullable(importDto.getAccountType()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL));
            Optional.ofNullable(importDto.getShopName()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL));
            Optional.ofNullable(importDto.getCompleteDate()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL));
            Optional.ofNullable(importDto.getMoney()).orElseThrow(() -> new OperationRejectedException(OperationExceptionCodeEnum.ACCOUNT_NON_NULL));
        }
    }

    @Override
    public ImportResult<AccountQueryImportResult> handle(ImportCmd<AccountImportByManpowerQueryRequest> cmd) {
        return accountFacade.queryImport(cmd.getRequest());
    }
}
