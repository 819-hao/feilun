package com.seeease.flywheel.serve.financial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.financial.request.AccountsPayableAccountingQueryRequest;
import com.seeease.flywheel.financial.result.AccountsPayableAccountingPageResult;
import com.seeease.flywheel.serve.financial.entity.AccountsPayableAccounting;
import com.seeease.flywheel.serve.financial.enums.FinancialStatusEnum;
import com.seeease.flywheel.serve.financial.enums.ReceiptPaymentTypeEnum;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author edy
 * @description 针对表【accounts_payable_accounting】的数据库操作Service
 * @createDate 2023-05-10 10:13:56
 */
public interface AccountsPayableAccountingService extends IService<AccountsPayableAccounting> {
    Page<AccountsPayableAccountingPageResult> page(AccountsPayableAccountingQueryRequest request);

    /**
     * 根据关联单号、状态、类型查找应收应付单
     *
     * @param originSerialNo
     * @param statusList
     * @param typeList
     * @return
     */
    List<AccountsPayableAccounting> selectListByOriginSerialNoAndStatusAndType(String originSerialNo, List<FinancialStatusEnum> statusList, List<ReceiptPaymentTypeEnum> typeList);

    /**
     * 根据申请打款单号、状态、类型查找应收应付单
     *
     * @param originSerialNo
     * @param statusList
     * @param typeList
     * @return
     */
    List<AccountsPayableAccounting> selectListByAfpSerialNoAndStatusAndType(String originSerialNo, List<FinancialStatusEnum> statusList, List<ReceiptPaymentTypeEnum> typeList);

    /**
     * 批量审核
     *
     * @param ids
     * @param auditDescription
     * @param userName
     */
    void batchAudit(List<Integer> ids, String auditDescription, String userName);

    /**
     * 创建应收应付
     *
     * @param originSerialNo
     * @param typeEnum
     * @param pendingReview
     * @param stockIds
     * @param subtract
     * @param b
     */
    List<Integer> createApa(String originSerialNo, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum pendingReview, List<Integer> stockIds, BigDecimal subtract, boolean b);

    void updateStatusByAfpSerialNo(String afpSerialNo, Integer currStatus, Integer toStatus);

    /**
     * 根据采购退货创建应收应付
     *
     * @param serialNo
     * @param returnId
     * @param stockIds
     * @param prePaidAmount
     * @param returnPendingReview
     */
    void createApaByReturn(String serialNo, Integer returnId, List<Integer> stockIds, ReceiptPaymentTypeEnum prePaidAmount, FinancialStatusEnum returnPendingReview);

    /**
     * @param map
     * @param typeEnum
     * @param pendingReview
     */
    List<Integer> createSaleApa(Map<String, List<Integer>> map, ReceiptPaymentTypeEnum typeEnum, FinancialStatusEnum pendingReview);

    /**
     * @param map
     * @param preReceiveAmount
     * @param pendingReview
     */
    void createSaleApaByReturn(Map<Integer, List<Integer>> map, ReceiptPaymentTypeEnum preReceiveAmount, FinancialStatusEnum pendingReview);

    /**
     *
     * @param stockSnList
     * @param amountPayable
     * @return
     */
    List<AccountsPayableAccounting> selectListByStockSnAndType(List<Integer> stockSnList, ReceiptPaymentTypeEnum amountPayable);

    void createSpecialApa(BillSaleOrder saleOrder, ReceiptPaymentTypeEnum amountReceivable, FinancialStatusEnum audited, List<Integer> list, BigDecimal decimal, boolean b);
}
