package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/3 14:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderListForBillResult implements Serializable {

    /**
     * 订单来源
     * 就是 shopName
     */
    private String locationName;

    /**
     * 是否国检
     */
    private Integer CIQ;
    /**
     * 单号
     */
    private String serialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 创建时间
     */
    private String createdTime;



//    private String parentSerialNo;
//
//
//    /**
//     * 创建人
//     */
//    private String createdBy;
//
//    /**
//     * 类型
//     */
//    private Integer saleType;
//
//    /**
//     * 方式
//     */
//    private Integer saleMode;
//
//    private Integer paymentMethod;

    private List<StockDTO> list;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockDTO implements Serializable{
        /**
         * 品牌
         */
        private String brandName;

        /**
         * 系列
         */
//        private String seriesName;

        /**
         * 型号
         */
        private String model;

        /**
         * 成色
         */
//        private String finess;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 附件列表
         */
        private String attachment;

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        /**
         * 抖音抽检码
         */
        private String spotCheckCode;
    }

//    private Integer stockId;
//    /**
//     * 公价
//     */
//    private BigDecimal pricePub;
//
//    /**
//     * 成本价
//     */
//    private BigDecimal consignmentPrice;
//
//    /**
//     * 同行寄售预计成交价
//     */
//    private BigDecimal preClinchPrice;
//
//    /**
//     * tob价格
//     */
//    private BigDecimal tobPrice;
//
//    /**
//     * toc价
//     */
//    private BigDecimal tocPrice;
//
//    /**
//     * 吊牌价
//     */
//    private BigDecimal tagPrice;



//    /**
//     * 联系人
//     */
//    private String customerName;
//
//    /**
//     * 状态
//     */
//    private Integer saleState;
//
//
//
//    private String finishTime;
//
//    private Integer saleTime;
//
//    /**
//     * 订单来源
//     */
//    private String shopName;
//
//    private Integer shopId;
//
//    private Integer customerId;
//    /**
//     * 购买原因
//     */
//    private Integer buyCause;
    /**
     * 联系人
     */
    private String contactName;

//    private Integer contactId;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

//    /**
//     * 销售渠道
//     */
//    private Integer saleChannel;
//
//    /**
//     * 客户联系人
//     */
//    private Integer customerContactId;
//
//    /**
//     * 第一销售人
//     */
//    private Integer firstSalesman;
//    private String firstSalesmanName;
//
//    /**
//     * 第二销售人
//     */
//    private Integer secondSalesman;
//    private String secondSalesmanName;
//    /**
//     * 第三销售人
//     */
//    private Integer thirdSalesman;
//    private String thirdSalesmanName;
//
//    /**
//     * 发货位置
//     */
//    private Integer deliveryLocationId;
//
//    /**
//     * 单行状态
//     */
//    private Integer saleLineState;
//
//
//
//    /**
//     * 经营权
//     */
//    private Integer rightOfManagement;
//    private String rightOfManagementName;
//    /**
//     * 分成比例 null 代表不分成
//     */
//    private BigDecimal proportion;
//    private BigDecimal totalPrice;
//
//    /**
//     *
//     */
//    private BigDecimal gmvPerformance;
//
//    /**
//     * 成交价格大于两万 1200 小于是600
//     */
//    private BigDecimal strapReplacementPrice;
//
//    /**
//     * 表带类型
//     */
//    private String strapMaterial;
//    /**
//     * 表节
//     */
//    private String watchSection;
//    /**
//     * 是否回购 1:是
//     */
//    private Integer isCounterPurchase;
//
//    /**
//     * 是否有回顾政策 1:是
//     */
//    private Integer isRepurchasePolicy;
//
//
//
////    private Integer locationId;
//
//    private BigDecimal marginPrice;//差额
//    private Integer balanceDirection;//差额类型
//
//    /**
//     * 商品编码
//     */
//    private String wno;
//    private String consignmentSaleFinishTime;

}
