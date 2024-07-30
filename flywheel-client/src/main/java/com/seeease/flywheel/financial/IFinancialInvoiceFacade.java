package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
public interface IFinancialInvoiceFacade {


    /**
     * 创建开票记录
     *
     * @param request
     * @return
     */
    FinancialInvoiceCreateResult create(FinancialInvoiceCreateRequest request);

    PageResult<FinancialInvoicePageResult> query(FinancialInvoiceQueryRequest request);

    void update(FinancialInvoiceUpdateRequest request);

    FinancialInvoiceDetailResult detail(FinancialInvoiceDetailRequest request);

    PageResult<FinancialInvoiceRecordPageResult> approvedMemo(FinancialInvoiceRecordRequest request);

    PageResult<FinancialInvoiceQueryByConditionResult> queryByCondition(FinancialInvoiceQueryByConditionRequest request);

    List<FinancialInvoiceDetailResult.LineDto> export(FinancialInvoiceQueryByConditionRequest request);

    void cancel(FinancialInvoiceCancelRequest request);

    List<PurchaseSubjectResult> queryInvoiceSubject();

    PageResult<FinancialInvoiceStockInfosResult> stockInfos(FinancialInvoiceStockInfosRequest request);

    FinancialInvoiceMaycurResult maycurInvoice(FinancialInvoiceMaycurRequest build);

    void uploadInvoice(FinancialInvoiceUploadInvoiceRequest request);
}
