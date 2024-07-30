package com.seeease.flywheel.financial;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseCancelRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseFlushingRequest;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseQueryByConditionRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseFlushingCancelResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseFlushingCreateResult;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseQueryByConditionResult;

/**
 * @author wbh
 * @date 2023/2/27
 */
public interface IFinancialInvoiceReverseFacade {

    PageResult<FinancialInvoiceReverseQueryByConditionResult> queryByCondition(FinancialInvoiceReverseQueryByConditionRequest request);

    PageResult<FinancialInvoiceReverseQueryByConditionResult> export(FinancialInvoiceReverseQueryByConditionRequest request);

    FinancialInvoiceReverseFlushingCreateResult flushing(FinancialInvoiceReverseFlushingRequest request);
    FinancialInvoiceReverseFlushingCancelResult cancel(FinancialInvoiceReverseCancelRequest request);

}
