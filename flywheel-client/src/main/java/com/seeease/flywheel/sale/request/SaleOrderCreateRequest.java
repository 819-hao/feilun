package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
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
public class SaleOrderCreateRequest implements Serializable {
    /**
     * 父订单编号
     */
    private String parentSerialNo;
    /**
     *
     */
    private String serialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;
    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    /**
     * 类型 1同行 2个人
     */
    private Integer saleType;

    private Integer saleSource;

    /**
     * 销售方式
     */
    private Integer saleMode;

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

    /**
     * 第二销售人
     */
    private Integer secondSalesman;

    /**
     * 第三销售人
     */
    private Integer thirdSalesman;

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
     * 总销售成本
     */
    private BigDecimal totalSalePrice;

    /**
     * 数量
     */
    private Integer saleNumber;

    /**
     * 销售门店id
     */
    private Integer shopId;

    /**
     * 发货位置
     */
    private Integer deliveryLocationId;

    /**
     * 备注
     */
    private String remarks;

    /**
     *
     */
    private Integer customerId;

    /**
     * 单据详情
     */
    private List<BillSaleOrderLineDto> details;

    /**
     * 确认负责人
     */
    private String owner;

    /**
     * 收货人信息
     */
    private ReceiverInfo receiverInfo;

    /**
     * 是否需要确认
     */
    private boolean saleConfirm;

    /**
     * 指定的创建人，默认取登陆用户
     */
    private PrescriptiveCreator creator;

    /**
     * 抖音列表ids
     */
    private List<Integer> douYinOrderIds;

    private List<Integer> kuaiShouOrderIds;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillSaleOrderLineDto implements Serializable {

        /**
         * 第三方子订单
         */
        private String subOrderCode;
        /**
         * 抖音抽检码
         */
        private String spotCheckCode;

        /**
         *
         */
        private Integer scInfoId;
        /**
         * 预售型号
         */
        private String model;

        /**
         * 型号编码
         */
        private String modelCode;

        /**
         * 预售表身号
         */
        private String stockSn;

        /**
         * 型号id
         */
        private Integer goodsId;

        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 成本价
         */
        private BigDecimal consignmentPrice;

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 经营权
         */
        private Integer rightOfManagement;

        /**
         *
         */
        private BigDecimal gmvPerformance;

        /**
         * 活动价格
         */
        private BigDecimal promotionPrice;

        /**
         * 活动寄售价格
         */
        private BigDecimal promotionConsignmentPrice;

        /**
         * 行情价
         */
        private BigDecimal marketsPrice;

        /**
         * 表带类型
         */
        private String strapMaterial;

        private String finess;

        /**
         * 表节
         */
        private String watchSection;
        /**
         * 是否维修 是否收取表带更换费
         */
        private Integer whetherFix;
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
         * 回购政策快照
         */
        private List<BuyBackPolicyInfo> buyBackPolicy;

        /**
         * 回购url
         */
        private String repurchasePolicyUrl;

        /**
         * 同行寄售预计成交价
         */
        private BigDecimal preClinchPrice;

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
        private BigDecimal totalPrice;
        /**
         * 质保年限
         */
        private Integer warrantyPeriod;
        /**
         * 公价
         */
        private BigDecimal pricePub;

        private Integer locationId;
        /**
         * 备注
         */
        private String remarks;

        /**
         * 是否允许低价销售(默认0:不允许 2:允许低于B价销售 3:允许低于C价销售)
         */
        private Integer isUnderselling;

        /**
         * 锁库门店
         */
        private Integer lockDemand;

        /**
         * 经营类型：1-综合小表、2-大表
         */
        private Integer brandBusinessType;

        /**
         * 滞后确认表身号
         */
        private boolean delayStockSn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiverInfo implements Serializable {

        /**
         * 收件⽅地址
         */
        private String receiverAddress;

        /**
         * 收件⼈名称
         */
        private String receiverName;
        /**
         * 收件⼈⼿机
         */
        private String receiverMobile;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptiveCreator implements Serializable {

        /**
         * 创建人id
         */
        private Integer createdId;

        /**
         * 创建人
         */
        private String createdBy;
    }

    /**
     * 销售备注
     */
    private String saleRemarks;

}
