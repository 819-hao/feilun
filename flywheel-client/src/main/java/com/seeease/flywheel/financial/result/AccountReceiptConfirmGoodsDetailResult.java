package com.seeease.flywheel.financial.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 确认收款单---订单详情/商品详情
 */
@Data
public class AccountReceiptConfirmGoodsDetailResult implements Serializable {

    private Integer id;
    private Integer collectionType;
    /**
     * 创建人
     */
    private String applicant;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicantTime;
    /**
     * 订单编号
     */
    private String serialNo;
    /**
     * 驳回原因
     */
    private String rejectionCause;
    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;
    /**
     * 期末寄售
     */
    private BigDecimal finalConsignmentMargin;
    /**
     * 正常余额
     */
    private BigDecimal accountBalance;
    /**
     * 期末余额
     */
    private BigDecimal finalAccountBalance;
    private String customerName;
    private String contactsName;
    private String phone;
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
