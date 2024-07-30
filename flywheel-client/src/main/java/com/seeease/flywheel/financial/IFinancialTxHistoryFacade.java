package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.TxHistoryDeleteRequest;
import com.seeease.flywheel.financial.request.TxHistoryImportRequest;
import com.seeease.flywheel.financial.request.TxHistoryQueryRequest;
import com.seeease.flywheel.financial.result.TxHistoryQueryResult;

public interface IFinancialTxHistoryFacade {


    void importDate(TxHistoryImportRequest request);

    PageResult<TxHistoryQueryResult> page(TxHistoryQueryRequest request);

    /**
     * 批量删除
     *
     * @param request
     */
    void remove(TxHistoryDeleteRequest request);
}
