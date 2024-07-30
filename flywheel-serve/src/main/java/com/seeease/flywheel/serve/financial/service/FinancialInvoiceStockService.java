package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceStock;

/**
 * @author edy
 * @description 针对表【financial_invoice_stock】的数据库操作Service
 * @createDate 2023-10-19 09:52:41
 */
public interface FinancialInvoiceStockService extends IService<FinancialInvoiceStock> {

    void deleteByInvoiceId(Integer id);

    FinancialInvoiceStock getOneByStockIdAndLineId(Integer stockId, Integer lineId);
}
