package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanDetailsResult implements Serializable {

    /**
     * 采购id
     */
    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 需求方
     */
    private String demanderStoreName;

    private Integer demanderStoreId;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 计划开始时间
     */
    private String planStartTime;
    /**
     * 计划结束时间
     */
    private String planEndTime;
    /**
     * 允许修改时间
     */
    private String enableChangeTime;
    /**
     * 选品时间
     */
    private String selectionTime;
    /**
     * 需求提交时间
     */
    private String demandStartTime;
    /**
     * 预估到货时间
     */
    private String estimatedDeliveryTime;
    /**
     * 业务类型：业务类型：0-默认其他,1-新表集采
     */
    private Integer businessType;

    /**
     * 采购行信息
     */
    private List<PurchasePlanLineVO> lines;

    @Data
    public static class PurchasePlanLineVO implements Serializable {
        /**
         * 详情id
         */
        private Integer id;

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
         * 主图
         */
        private String image;
        /**
         * 公价
         */
        private BigDecimal pricePub;

        /**
         * 型号id
         */
        private Integer goodsId;
        /**
         * 优先级
         */
        private Integer priority;
        /**
         * 采购数量
         */
        private Integer planNumber;
        /**
         * 当前行情价
         */
        private BigDecimal currentPrice;

        /**
         * 20年行情价
         */
        private BigDecimal twoZeroFullPrice;

        /**
         * 22年行情价
         */
        private BigDecimal twoTwoFullPrice;

        /**
         * 建议采购价
         */
        private BigDecimal suggestedPurchasePrice;
    }
}
