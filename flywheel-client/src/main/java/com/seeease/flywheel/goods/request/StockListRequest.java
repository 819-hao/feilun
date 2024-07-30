package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockListRequest extends PageRequest {


    private List<Integer> idList;
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
     * 表身号
     */
    private String stockSn;

    /**
     * 商品状态
     */
    private Integer stockStatus;

    /**
     * 使用场景
     */
    private UseScenario useScenario;

    /**
     * 经营权
     */
    private Integer rightOfManagement;

    /**
     * TOB_C(0, "B/C可同销"),
     * TOB(1, "仅B端销售"),
     * TOC(2, "仅C端销售"),
     */
    private Integer salesPriority;

    private Integer customerId;

    private Integer purchaseType;

    private String serialNo;

    private Integer storeId;

    private Integer stockSrc;

    /**
     * 排除商品位置，品牌调拨场景
     */
    private Integer excludedLocationId;

    /**
     * 商品状态
     */
    private List<Integer> stockStatusList;

    //采购退货

    private List<Integer> stockIdList;

    /**
     * 型号
     */
    private List<Integer> goodsIdList;

    /**
     * 成色
     */
    private String finess;

    /**
     * 商品位置
     */
    private Integer locationId;

    /**
     * 门店库龄
     */
    private Integer startStorageAge;


    /**
     * 门店库龄
     */
    private Integer endStorageAge;

    /**
     * 总库龄
     */
    private Integer startTotalStorageAge;

    /**
     * 总库龄
     */
    private Integer endTotalStorageAge;
    /**
     * 商品编码
     */
    private String wno;

    private boolean filterSpecificBrand;

    private List<AutoSelectDTO> autoSelect;

    /**
     * 无参数的
     */
    private Boolean single;

    /**
     * 颜色
     */
    private String colour;

    /**
     * 材质
     */
    private String material;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 适用腕表型号
     */
    private String gwModel;

    private Integer seriesType;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AutoSelectDTO implements Serializable {
        /**
         * goodsId
         */
        private Integer goodsId;
        /**
         * 型号
         */
        private String model;

        /**
         * 无参数的
         */
        private Boolean single;

        /**
         * 颜色
         */
        private String colour;

        /**
         * 材质
         */
        private String material;

        /**
         * 尺寸
         */
        private String size;

        /**
         * 适用腕表型号
         */
        private String gwModel;

        /**
         * 数量
         */
        private Integer quantity;
    }

    /**
     * 表身号批量
     */
    private List<String> stockSnList;


    public enum UseScenario {
        /**
         * 调拨场景
         */
        ALLOCATE,
        /**
         * 调拨任务
         */
        ALLOCATE_TASK,
        //统一发起退货
        PURCHASE_RETURN,
        //原单发起退货
        PURCHASE_RETURN_STOCK,
        SALE,
        /**
         * 订金销售
         */
        MONEY_SALE,
        /**
         * 批量寄售结算
         */
        BATCH_CONSIGNMENT_SETTLEMENT,

        /**
         * 申请开票
         */
        APPLY_FINANCIAL_INVOICE,
        /**
         * 同行集采结算
         */
        GROUP_CONSIGNMENT_SETTLEMENT,
    }
}
