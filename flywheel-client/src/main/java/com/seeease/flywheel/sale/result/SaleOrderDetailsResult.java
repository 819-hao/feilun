package com.seeease.flywheel.sale.result;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import com.seeease.flywheel.sale.entity.SaleDeliveryVideoData;
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
public class SaleOrderDetailsResult implements Serializable {

    private String shopAddress;
    /**
     * id
     */
    private Integer id;

    /**
     * 类型
     */
    private Integer saleType;

    /**
     * 方式
     */
    private Integer saleMode;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 总采购成本
     */
    private BigDecimal totalSalePrice;

    /**
     * 采购单状态
     */
    private Integer saleState;

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
     *
     */
    private String parentSerialNo;
    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    private Integer saleSource;

    /**
     * 销售渠道
     */
    private Integer saleChannel;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 第一销售人
     */
    private Integer firstSalesman;
    private String firstSalesmanName;

    /**
     * 第二销售人
     */
    private Integer secondSalesman;
    private String secondSalesmanName;
    /**
     * 第三销售人
     */
    private Integer thirdSalesman;
    private String thirdSalesmanName;
    /**
     * 订金金额
     */
    private BigDecimal deposit;

    /**
     * 付款方式
     */
    private Integer paymentMethod;

    /**
     * 购买原因
     */
    private Integer buyCause;

    /**
     * 数量
     */
    private Integer saleNumber;

    /**
     * 发货位置
     */
    private Integer deliveryLocationId;

    private String finishTime;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 行信息
     */
    private List<SaleOrderLineVO> lines;

    /**
     * 客户信息是否已加密
     */
    private boolean contactEncrypted;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    /**
     * 创建人id
     */
    private Integer createdId;

    @Data
    public static class SaleOrderLineVO implements Serializable {
        /**
         * 详情id
         */
        private Integer id;
        /**
         * 表id
         */
        private Integer stockId;
        /**
         * 表身号
         */
        private String stockSn;
        private Integer belongId;
        private String belongName;
        private String subjectUrl;
        /**
         * 销售单号
         */
        private String serialNo;
        /**
         * 成色
         */
        private String finess;

        /**
         * 单行状态
         */
        private Integer saleLineState;

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
         * 行情价
         */
        private BigDecimal marketsPrice;
        /**
         * 活动价格
         */
        private BigDecimal promotionConsignmentPrice;
        /**
         * 表带类型
         */
        private String strapMaterial;

        /**
         * 快递单号
         */
        private String expressNumber;

        /**
         * 表节
         */
        private String watchSection;

        /**
         * 成交价格大于两万 1200 小于是600
         */
        private BigDecimal strapReplacementPrice;

        /**
         * 是否回购 1:是
         */
        private Integer isCounterPurchase;

        /**
         * 是否有回顾政策 1:是
         */
        private Integer isRepurchasePolicy;

        /**
         * 回购url
         */
        private String repurchasePolicyUrl;

        /**
         * 同行寄售预计成交价
         */
        private BigDecimal preClinchPrice;
        /**
         * 最新结算价
         */
        private BigDecimal newSettlePrice;

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

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 状态
         */
        private Integer state;

        private String locationName;
        private Integer locationId;

        /**
         * 备注
         */
        private String remarks;
        private String buyBackPolicy;
        private BigDecimal marginPrice;//差额
        private Integer balanceDirection;//差额类型
        private List<BuyBackPolicyInfo> list;

        private Integer saleId;

        /**
         * 商品编码
         */
        private String wno;

        /**
         * 机芯类型
         */
        private String movement;

        /**
         * 寄售完成时间
         */
        private String consignmentSaleFinishTime;
        private String consignmentSettlementOperator;

        /**
         * 第三方子订单
         */
        private String subOrderCode;

        /**
         * 质保年限
         */
        private Integer warrantyPeriod;

        /**
         * 型号主图
         */
        private String image;

        private Integer whetherInvoice;

        /**
         * 质检视频
         */
        private List<SaleDeliveryVideoData> rcData;
    }

    /**
     * 销售备注
     */
    private String saleRemarks;
}
