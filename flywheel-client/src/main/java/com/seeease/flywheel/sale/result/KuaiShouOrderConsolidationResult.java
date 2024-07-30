package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KuaiShouOrderConsolidationResult implements Serializable {

    /**
     * 抖音列表ids
     */
    private List<Integer> kuaiShouOrderIds;
    /**
     * 类型 1同行 2个人
     */
    private Integer saleType;
    /**
     * 销售方式
     */
    private Integer saleMode;

    /**
     * 销售渠道
     */
    private Integer saleChannel;
    /**
     * 付款方式
     */
    private Integer paymentMethod;
    /**
     * 门店id
     */
    private Integer shopId;
    /**
     * 购买原因
     */
    private Integer buyCause;
    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;
    /**
     * 密文收件人电话
     */
    private String encryptPostTel;

    /**
     * 密文收件人姓名
     */
    private String encryptPostReceiver;

    /**
     * 密文收件地址省市区
     */
    private String encryptAddrArea;

    /**
     * 密文收件地址
     */
    private String encryptDetail;

    /**
     * 密文收件人电话
     */
    private String maskPostTel;

    /**
     * 密文收件人姓名
     */
//    private String maskPostReceiver;
//
//    /**
//     * 密文收件地址省市区
//     */
//    private String maskAddrArea;
//
//    /**
//     * 密文收件地址
//     */
//    private String maskDetail;


    private String accessToken;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 单据详情
     */
    private List<BillSaleOrderLineDto> details;

    /**
     * 收货人信息
     */
    private ReceiverInfo receiverInfo;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillSaleOrderLineDto implements Serializable {

        /**
         * 三方子单号
         */
        private String subOrderCode;
        /**
         * 抖音抽检码
         */
        private String spotCheckCode;
        /**
         * 型号唯一编码
         */
        private String modelCode;
        /**
         * 预售型号
         */
        private String model;

        /**
         * 品牌
         */
        private String brandName;
        /**
         * 系列
         */
        private String seriesName;

        /**
         * 成交价
         */
        private BigDecimal clinchPrice;

        private Integer scInfoId;
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
}
