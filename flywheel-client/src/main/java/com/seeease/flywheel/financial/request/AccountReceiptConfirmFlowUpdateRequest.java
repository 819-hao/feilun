package com.seeease.flywheel.financial.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 确认收款---提交流水
 */
@Data
public class AccountReceiptConfirmFlowUpdateRequest implements Serializable {


    private Integer id;

    /**
     * 流水单主键id
     */
    private List<FinancialStatementUpdateRequest> financialStatementList;

    @Data
    public static class FinancialStatementUpdateRequest implements Serializable {

        /**
         * $column.columnComment
         */
        private Integer id;

        /**
         * 财务流水号
         */
        private String serialNo;

        /**
         * 收款时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date collectionTime;

        /**
         * 流水归属id
         */
        private Integer shopId;

        /**
         * 收款主体id
         */
        private Integer subjectId;

        /**
         * 收款主体
         */
        private String subjectName;

        /**
         * 付款人
         */
        private String payer;


        /**
         * 实收金额
         */
        private BigDecimal fundsReceived;

        /**
         * 手续费
         */
        private BigDecimal procedureFee;

        /**
         * 收款金额
         * ---PC端绑定流水使用金额
         */
        private BigDecimal receivableAmount;

        /**
         * 待核销金额
         */
        private BigDecimal waitAuditPrice;

        /**
         * 状态
         */
        private Integer status;
    }

}
