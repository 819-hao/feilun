package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.*;
import com.seeease.flywheel.financial.result.*;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;

/**
 * @author edy
 * @description 针对表【financial_invoice】的数据库操作Service
 * @createDate 2023-10-19 09:35:12
 */
public interface FinancialInvoiceService extends IService<FinancialInvoice> {

    Page<FinancialInvoicePageResult> queryPage(FinancialInvoiceQueryRequest request);

    void update(FinancialInvoiceUpdateRequest request);

    FinancialInvoiceDetailResult detail(FinancialInvoiceDetailRequest request);

    Page<FinancialInvoiceRecordPageResult> approvedMemo(FinancialInvoiceRecordRequest request);

    Page<FinancialInvoiceQueryByConditionResult> queryByCondition(FinancialInvoiceQueryByConditionRequest request);

    void maycurInvoice(FinancialInvoice invoice, FinancialInvoiceStateEnum invoiceStateEnum, FinancialInvoiceStateEnum stateEnum);

    Page<FinancialInvoiceStock> stockInfos(FinancialInvoiceStockInfosRequest request);
}
