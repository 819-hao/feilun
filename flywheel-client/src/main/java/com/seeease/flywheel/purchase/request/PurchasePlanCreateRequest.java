package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 采购创建的基类
 *
 * @author Tiro
 * @date 2023/1/7
 */
@Data
public class PurchasePlanCreateRequest implements Serializable {
    //**********************前置********************
    /**
     * 单号
     */
    private String serialNo;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 备注
     */
    private String remarks;
    /**
     * 计划开始时间
     */
    private String planStartTime;
    /**
     * 计划结束时间
     */
    private String planEndTime;
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
     * 单据详情
     */
    private List<BillPurchasePlanLineDto> details;
    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 允许修改时间
     */
    private Date enableChangeTime;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPurchasePlanLineDto implements Serializable {

        /**
         * 商品型号id
         */
        private Integer goodsId;
        /**
         * 采购数量
         */
        private Integer planNumber;
        /**
         * 公价
         */
        private BigDecimal pricePub;

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

        /**
         * 备注
         */
        private String remarks;

        /**
         * 优先级
         */
        private Integer priority;
    }

}
