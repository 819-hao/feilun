package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderDetailsResult implements Serializable {

    /**
     * id
     */
    private Integer id;

    private Integer saleId;
    /**
     * 类型
     */
    private Integer saleReturnType;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 成本
     */
    private BigDecimal totalSaleReturnPrice;

    /**
     * 状态
     */
    private Integer saleReturnState;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 订单来源
     */
    private String shopName;

    private Integer shopId;

    /**
     * 客户
     */
    private String customerName;

    private Integer customerId;

    /**
     * 联系人
     */
    private String contactName;

    private Integer contactId;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 第一销售人
     */
    private String firstSalesmanName;

    /**
     * 第二销售人
     */
    private String secondSalesmanName;
    /**
     * 第三销售人
     */
    private String thirdSalesmanName;
    private String parentSerialNo;
    private String saleSerialNo;
    /**
     * 是否错单退货 0否 1是
     */
    private Integer refundFlag;

    /**
     * 行信息
     */
    private List<SaleReturnOrderLineVO> lines;

    @Data
    public static class SaleReturnOrderLineVO implements Serializable {
        /**
         * 详情id
         */
        private Integer id;
        private Integer saleLineId;
        /**
         * 表id
         */
        private Integer stockId;
        /**
         * 表身号
         */
        private String stockSn;
        /**
         * 成色
         */
        private String finess;

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
         * 公价
         */
        private BigDecimal pricePub;

        /**
         * 型号id
         */
        private Integer goodsId;

        /**
         * 附件列表
         */
        private String attachment;

        /**
         * 经营权
         */
        private Integer rightOfManagement;
        private String rightOfManagementName;
        /**
         * 分成比例 null 代表不分成
         */
        private BigDecimal proportion;
        private BigDecimal totalPrice;

        /**
         *
         */
        private BigDecimal gmvPerformance;

        /**
         * tob价格
         */
        private BigDecimal tobPrice;

        /**
         * toc价
         */
        private BigDecimal tocPrice;

        /**
         * 吊牌价
         */
        private BigDecimal tagPrice;

        /**
         * 成本价
         */
        private BigDecimal consignmentPrice;
        private BigDecimal preClinchPrice;

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 状态
         */
        private Integer saleReturnLineState;

        private String locationName;

        private BigDecimal marginPrice;//差额
        private Integer balanceDirection;//差额类型

        /**
         * 最新结算价
         */
        private BigDecimal newSettlePrice;

        private Integer saleMode;

        /**
         * 本次销售单号（个人置换，个人回购）
         */
        private String saleSerialNo;

        private BigDecimal returnPrice;
        private Integer whetherInvoice;
        private Integer whetherOperate;
    }


}
