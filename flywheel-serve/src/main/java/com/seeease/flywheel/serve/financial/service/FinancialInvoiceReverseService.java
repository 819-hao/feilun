package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseQueryByConditionRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseQueryByConditionResult;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceReverse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author edy
* @description 针对表【financial_invoice_reverse】的数据库操作Service
* @createDate 2023-11-30 09:49:04
*/
public interface FinancialInvoiceReverseService extends IService<FinancialInvoiceReverse> {

    Page<FinancialInvoiceReverseQueryByConditionResult> queryByCondition(FinancialInvoiceReverseQueryByConditionRequest request);
}
