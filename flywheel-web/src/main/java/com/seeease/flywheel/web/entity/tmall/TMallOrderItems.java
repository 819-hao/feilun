package com.seeease.flywheel.web.entity.tmall;

import lombok.Data;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * 天猫订单货品信息
 *
 * @author Tiro
 * @date 2023/3/24
 */
@Data
public class TMallOrderItems {
    /**
     * 订单货品信息列表
     */
    private List<OrderItem> orderItem;


    @Data
    public static class OrderItem {
        /**
         * 履约⼦单号
         */
        @XmlTransient
        private String subOrderCode;
        /**
         * 货品ID
         */
        @XmlTransient
        private String scItemId;
        /**
         * 货品商家编码
         */
        @XmlTransient
        private String outerId;
        /**
         * 货品条形码
         */
        @XmlTransient
        private String barCode;
        /**
         * 货品数量
         */
        @XmlTransient
        private Integer quantity;
        /**
         * 货品名称
         */
        @XmlTransient
        private String scItemName;

        /**
         * 交易金额(分)
         */
        @XmlTransient
        private String itemAmount;
        /**
         * 交易主单号
         */
        @XmlTransient
        private String tradeOrerId;
        /**
         * 交易⼦单号
         */
        @XmlTransient
        private String subTradeOrderId;
//        /**
//         * 交易⼦单号
//         */
//        @XmlTransient
//        private String tcSubOrderId;

        /**
         * 退款单号
         */
        @XmlTransient
        private String refundId;

        /**
         * 货品计划退回数量
         */
        @XmlTransient
        private Integer planReturnQuantity;

        /**
         * 外部ERP订单行号
         */
        @XmlTransient
        private String erpOrderLine;

        /**
         * 退款原因
         */
        @XmlTransient
        private String refundReason;
    }
}