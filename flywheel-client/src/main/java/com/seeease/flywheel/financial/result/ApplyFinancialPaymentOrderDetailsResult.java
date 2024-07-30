package com.seeease.flywheel.financial.result;

import com.seeease.flywheel.sale.result.SaleReturnOrderDetailsResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentOrderDetailsResult implements Serializable {

    private Integer id;
    /**
     * 创建人
     */
    private String applicant;
    /**
     * 创建时间
     */
    private String applicantTime;
    /**
     * 订单编号
     */
    private String serialNo;
    /**
     * 行信息
     */
    private List<lineVO> lines;

    @Data
    public static class lineVO implements Serializable {

        /**
         * 关联单号
         */
        private String originSerialNo;
        /**
         * 品牌
         */
        private String brandName;
        /**
         * 系列
         */
        private String seriesName;
        /**
         * 型号
         */
        private String model;
        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 附件
         */
        private String attachment;

        /**
         * 金额
         */
        private BigDecimal originPrice;
    }
}
