package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 个人销售退货单---申请退款
 * ---com.seeease.flywheel.serve.financial.service.impl.ApplyFinancialPaymentServiceImpl#create
 * (com.seeease.flywheel.financial.request.ApplyFinancialPaymentCreateRequest)
 */
@Data
public class SaleReturnOrderRefundRequest implements Serializable {

    /**
     * 退货单id
     */
    private Integer id;

    private String serialNo;

    private Integer customerId;

    private String customerName;

    private Integer contactId;

    private String contactName;

    private String contactPhone;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行开户行
     */
    private String bankAccount;

    /**
     * 银行卡号
     */
    private String bankCard;

    /**
     * 收款账户名
     */
    private String bankCustomerName;

    /**
     * 打款主体
     * 存在多个打款主体，使用的是打款主体的名称
     * 需要从Integer改成String
     */
    private String subjectPayment;

    private List<LineVO> lines;

    @Data
    public static class LineVO implements Serializable {
        /**
         * 表id
         */
        private Integer stockId;
        private BigDecimal returnPrice;
    }
}
