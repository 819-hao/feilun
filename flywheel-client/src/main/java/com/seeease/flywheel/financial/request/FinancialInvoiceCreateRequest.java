package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceCreateRequest implements Serializable {

    /**
     * 企业客户id
     */
    private Integer customerId;

    /**
     * 客户id
     */
    private Integer contactId;
    /**
     * 购买人姓名—customerName
     */
    private String customerName;
    /**
     * 地址
     */
    private String contactAddress;
    /**
     * 号码
     */
    private String contactPhone;
    /**
     * 创建时间
     */
    private String createdTime;
    /**
     * 销售平台—可空
     */
    private Integer saleChannel;
    private Integer shopId;
    /**
     * 客户邮箱
     */
    private String customerEmail;

    /**
     *
     */
    private String remarks;

    /**
     * 付款方式
     */
    private Integer paymentMethod;

    /**
     * 开票类型
     */
    private Integer invoiceType;

    /**
     * 开票抬头
     */
    private String invoiceTitle;
    /**
     * 开票人
     */
    private String invoiceUser;

    /**
     * 单位税号
     */
    private String unitTaxNumber;

    /**
     * 开票类型
     */
    private Integer invoiceOrigin;

    /**
     * 使用场景
     */
    private UseScenario useScenario;
    /**
     * 单据详情
     */
    private List<LineDto> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineDto implements Serializable {

        /**
         * 销售/退货 的行id
         */
        private Integer lineId;
        /**
         * 单号
         */
        private String serialNo;
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
         * 库存id
         */
        private Integer stockId;

        /**
         * 开票主体 开票公司
         */
        private String invoiceSubjectName;

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 商品归属
         */
        private Integer belongId;

    }

    public enum UseScenario {

        GR_XS,
        TH_XS,
        GR_XS_TH,
        TH_XS_TH,

    }
}
