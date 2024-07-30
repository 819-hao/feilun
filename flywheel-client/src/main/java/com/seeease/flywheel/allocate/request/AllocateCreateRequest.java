package com.seeease.flywheel.allocate.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateCreateRequest implements Serializable {
    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private Integer allocateType;

    /**
     * 调拨来源
     */
    private Integer allocateSource;

    /**
     * 调入方
     */
    private Integer toId;

    /**
     * 调入仓库
     */
    private Integer toStoreId;

    /**
     * 采购备注
     */
    private String remarks;

    /**
     * 调拨详情
     */
    private List<AllocateLineDto> details;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 品牌调拨任务
     */
    private boolean isBrandTask;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocateLineDto implements Serializable {

        /**
         * 调出方
         */
        private Integer fromId;

        /**
         * 调出仓库
         */
        private Integer fromStoreId;

        /**
         * 商品id
         */
        private Integer goodsId;

        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 成本
         */
        private BigDecimal costPrice;

        /**
         * 寄售价
         */
        private BigDecimal consignmentPrice;
        /**
         * 最新结算价
         */
        private BigDecimal newSettlePrice;
        /**
         * 调拨价
         */
        private BigDecimal transferPrice;

        /**
         * 物流单号
         */
        private String expressNumber;

        /**
         * 出库时间
         */
        private Date ckTime;

        /**
         * 原经营权
         */
        private Integer fromRightOfManagement;

        /**
         * 变更后经营权
         */
        private Integer toRightOfManagement;

        /**
         * 当前商品状态
         */
        private Integer fromStockStatus;

        /**
         * 保卡管理-是否已调拨
         */
        private Integer guaranteeCardManage;

        /**
         * 是否在保卡管理里
         */
        private Integer whetherCardManage;
    }
}
