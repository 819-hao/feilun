package com.seeease.flywheel.purchase.result;

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
 * @Date create in 2023/4/18 10:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseBuyBackResult implements Serializable {

    /**
     * 列表
     */
    private List<PurchaseBuyBackDto> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseBuyBackDto implements Serializable {

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 采购价
         */
        private BigDecimal purchasePrice;

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
         * 成色
         */
        private String finess;

        /**
         * 附件
         */
        private String attachment;

        /**
         * 表带材质
         */
        private String strapMaterial;

        /**
         * 表节
         */
        private String watchSection;

        /**
         * 门店名称
         */
        private String storeName;

        /**
         * 创建者
         */
        private String createBy;

        /**
         * 单号
         */
        private String serialNo;

        /**
         * 创建时间
         */
        private String createTime;

        /**
         * 采购状态
         * UNCONFIRMED(1, "待确认"),
         * UNDER_WAY(2, "进行中"),
         * COMPLETE(4, "已完成"),
         * CANCEL_WHOLE(3, "全部取消"),
         */
        private Integer purchaseState;

        /**
         * 0 无 1 有 2空白保卡
         */
        private Integer isCard;

        /**
         * 日期
         */
        private String warrantyDate;

        /**
         * 销售单号
         */
        private String saleSerialNo;

        /**
         * 参考回购价（回购）
         */
        private BigDecimal referenceBuyBackPrice;
        /**
         * 预计维修费（个人寄售，个人回购）
         */
        private BigDecimal planFixPrice;


        /**
         * 表带更换费（回购）
         */
        private BigDecimal watchbandReplacePrice;
    }
}
