package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 确认收款---提交流水
 */
@Data
public class AccountReceiptConfirmConfirmReceiptRequest implements Serializable {


    private Integer id;

    /**
     * 流水单主键id
     */
    private List<FinancialStatementUpdateRequest> financialStatementList;

    @Data
    public static class FinancialStatementUpdateRequest implements Serializable {

        /**
         * 财务流水号
         */
        private String serialNo;

        /**
         * 收款金额
         * ---PC端绑定流水使用金额
         */
        private BigDecimal receivableAmount;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckConfirmVO implements Serializable {
        /**
         *  确认收款单号
         */
        private String arcSerialNo;
        /**
         *  交易编号
         */
        private String fsSerialNo;
    }
}
