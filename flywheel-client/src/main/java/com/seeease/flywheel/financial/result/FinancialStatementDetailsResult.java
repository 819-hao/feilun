package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialStatementDetailsResult implements Serializable {


    private Integer id;

    /**
     * 财务流水号
     */
    private String serialNo;

    /**
     * 收款时间
     */
    private String collectionTime;

    /**
     * 门店id
     */
    private Integer shopId;
    private String shopName;

    /**
     * 收款主体
     */
    private Integer subjectId;
    private String subjectName;

    private List<LineVo> details;

    @Data
    public static class LineVo implements Serializable {

        /**
         * 关联确认收款
         */
        private String arcSerialNo;

        /**
         * 可用金额
         */
        private BigDecimal availableAmount;

        /**
         * 使用金额
         */
        private BigDecimal usedAmount;

        /**
         * 审核人
         */
        private String auditName;

        /**
         * 审核时间
         */
        private Date auditTime;

        /**
         * 审核说明
         */
        private String auditDescription;
    }

}
