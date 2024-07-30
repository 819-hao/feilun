package com.seeease.flywheel.web.extension.excel;

import com.alibaba.cola.extension.Extension;
import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.financial.IFinancialTxHistoryFacade;
import com.seeease.flywheel.financial.request.TxHistoryImportRequest;
import com.seeease.flywheel.goods.IStockPromotionFacade;
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
@Extension(bizId = BizCode.IMPORT, useCase = UseCase.TX_HISTORY)
public class TxHistoryImportExt implements ImportExtPtl<TxHistoryImportRequest, Void> {

    @DubboReference(check = false, version = "1.0.0")
    private IFinancialTxHistoryFacade facade;


    @Override
    public Class<TxHistoryImportRequest> getRequestClass() {
        return TxHistoryImportRequest.class;
    }

    @Override
    public void validate(ImportCmd<TxHistoryImportRequest> cmd) {

    }

    @Override
    public ImportResult<Void> handle(ImportCmd<TxHistoryImportRequest> cmd) {
        facade.importDate(cmd.getRequest());
        return new ImportResult<>();
    }


}
