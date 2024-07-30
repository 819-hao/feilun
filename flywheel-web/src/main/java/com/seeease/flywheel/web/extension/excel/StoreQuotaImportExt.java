package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.financial.IFinancialTxHistoryFacade;
import com.seeease.flywheel.financial.request.StoreQuotaImportRequest;
import com.seeease.flywheel.financial.request.TxHistoryImportRequest;
import com.seeease.flywheel.maindata.IStoreFacade;
import com.seeease.flywheel.web.common.excel.ImportCmd;
import com.seeease.flywheel.web.common.excel.ImportExtPtl;
import com.seeease.flywheel.web.extension.BizCode;
import com.seeease.flywheel.web.extension.UseCase;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 财务流水记录导入
 *
 * @author Tiro
 * @date 2023/3/30
 */
@Service
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.STORE_QUOTA)
public class StoreQuotaImportExt implements ImportExtPtl<StoreQuotaImportRequest, Void> {

    @DubboReference(check = false, version = "1.0.0")
    private IStoreFacade iStoreFacade;


    @Override
    public Class<StoreQuotaImportRequest> getRequestClass() {
        return StoreQuotaImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<StoreQuotaImportRequest> cmd) {

    }

    @Override
    public ImportResult<Void> handle(ImportCmd<StoreQuotaImportRequest> cmd) {
        iStoreFacade.importDate(cmd.getRequest());
        return new ImportResult<>();
    }


}
