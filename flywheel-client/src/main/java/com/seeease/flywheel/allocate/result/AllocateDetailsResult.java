package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateDetailsResult implements Serializable {
    /**
     * 调拨单id
     */
    private Integer id;
    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private Integer allocateType;

    /**
     * 调拨状态
     */
    private Integer allocateState;

    /**
     * 调拨来源
     */
    private Integer allocateSource;

    /**
     * 调出方
     */
    private String fromName;

    /**
     * 调入方
     */
    private String toName;

    /**
     * 调入仓库
     */
    private String toStoreName;

    /**
     * 总成本
     */
    private BigDecimal totalCostPrice;

    /**
     * 总寄售价
     */
    private BigDecimal totalConsignmentPrice;

    /**
     * 数量
     */
    private Integer totalNumber;

    /**
     * 采购备注
     */
    private String remarks;

    /**
     * 详情
     */
    private List<AllocateLineVO> details;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 是否来自调拨任务
     */
    private boolean isBrandTask;
    /**
     *  调入方地址
     */
    private String address;



    private BigDecimal totalProfit;

    @Data
    public static class AllocateLineVO implements Serializable {
        private BigDecimal profit;
        private BigDecimal newSettlePrice;
        private BigDecimal transferPrice;
        /**
         * 调拨单行id
         */
        private Integer id;

        /**
         * 调拨行状态
         */
        private Integer allocateLineState;

        /**
         * 商品id
         */
        private Integer goodsId;

        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 附件详情
         */
        private String attachment;

        /**
         * 成色
         */
        private String finess;

        /**
         * 成本
         */
        private BigDecimal costPrice;

        /**
         * 寄售价
         */
        private BigDecimal consignmentPrice;

        /**
         * 物流单号
         */
        private String expressNumber;

        /**
         * 出库时间
         */
        private String ckTime;

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
         * 保卡管理-是否已调拨
         */
        private Integer guaranteeCardManage;
    }
}
