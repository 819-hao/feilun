package com.seeease.flywheel.financial.result;

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
public class FinancialInvoiceDetailResult implements Serializable {

    private Integer id;

    /**
     *
     */
    private String serialNo;
    /**
     * 原关联开票单号
     */
    private String originalInvoiceSerialNo;
    /**
     * 开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 开票号码
     */
    private String invoiceNumber;
    private String originInvoiceNumber;

    /**
     * 开票时间
     */
    private String invoiceTime;

    /**
     * 开票人
     */
    private String invoiceUser;
    private String batchPictureUrl;

    /**
     *
     */
    private Integer totalNumber;

    /**
     * 开票主体
     */
    private Integer invoiceSubject;
    private String invoiceSubjectName;

    /**
     * 客户联系人id
     */
    private Integer customerContactsId;
    private Integer customerId;
    private String customerName;

    private String customerEmail;
    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 开票抬头
     */
    private String invoiceTitle;

    /**
     * 单位税号
     */
    private String unitTaxNumber;

    /**
     * 发票类型
     */
    private Integer invoiceType;

    /**
     * 开票类型
     */
    private Integer invoiceOrigin;

    /**
     *
     */
    private Integer state;

    private String remarks;
    /**
     * 申请人门店
     */
    private Integer shopId;

    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    private String result;

    /**
     * 商品信息
     */
    private List<LineDto> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineDto implements Serializable {
        /**
         * 库存id
         */
        private Integer stockId;
        /**
         * 关联单号
         */
        private String originSerialNo;

        /**
         * 金额
         */
        private BigDecimal originPrice;

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
         * 开票id
         */
        private Integer financialInvoiceId;

        /**
         *
         */
        private Integer forwardFiId;

        /**
         * 正向开票单号
         */
        private String forwardSerialNo;


        /**
         *
         */
        private String serialNo;
    }

}
