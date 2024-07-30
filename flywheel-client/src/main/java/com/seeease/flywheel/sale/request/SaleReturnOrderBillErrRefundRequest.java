package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 错单退货
 */
@Data
public class SaleReturnOrderBillErrRefundRequest implements Serializable {

    /**
     * 个人销售退货单id
     */
    private Integer id;

    private String serialNo;

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


        private Integer saleMode;

        /**
         * 本次销售单号（个人置换，个人回购）
         */
        private String saleSerialNo;

        private BigDecimal returnPrice;

    }
}
