package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.FinancialQueryAllRequest;
import com.seeease.flywheel.financial.result.FinancialExportResult;
import com.seeease.flywheel.financial.result.FinancialPageAllResult;
import com.seeease.flywheel.serve.financial.entity.FinancialGenerateDto;
import com.seeease.flywheel.serve.financial.entity.FinancialDocuments;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
* @author edy
* @description 针对表【financial_documents(财务单据)】的数据库操作Service
* @createDate 2023-03-27 09:52:56
*/
public interface FinancialDocumentsService extends IService<FinancialDocuments> {
    /**
     * 销售生成财务单
     *
     * @param financialGenerateDto
     */
    void generateSale(FinancialGenerateDto financialGenerateDto);

    /**
     * 用于天猫结算后的财务单据生成
     * @param financialGenerateDto
     */
    void generateSaleBalance(FinancialGenerateDto financialGenerateDto);

    /**
     * 退货生成财务单
     *
     * @param financialGenerateDto
     */
    void generateSaleReturn(FinancialGenerateDto financialGenerateDto);

    /**
     * 采购生成财务单据
     *
     * @param financialGenerateDto
     */
    void generatePurchase(FinancialGenerateDto financialGenerateDto);

    /**
     * 采购质检生成退货
     * @param dto
     */
    void generatePurchaseQtReturn(FinancialGenerateDto dto);

    void generatePurchaseReturn(FinancialGenerateDto financialGenerateDto);

    /**
     * 天猫结算退货 财务
     * @param financialGenerateDto
     */
    void generateSaleReturnBalance(FinancialGenerateDto financialGenerateDto);

    Page<FinancialPageAllResult> selectByFinancialQueryAllRequest(FinancialQueryAllRequest request);

    List<FinancialExportResult> selectExcelByFinancialDocumentsQueryDto(FinancialQueryAllRequest request);

    void generatePurchaseMarginCover(FinancialGenerateDto dto);
}
