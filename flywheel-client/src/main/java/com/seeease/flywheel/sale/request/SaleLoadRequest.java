package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 销售工作流挂载参数 todo
 * @Date create in 2023/9/7 15:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleLoadRequest implements Serializable {

    /**
     * 订单集合
     */
    private List<SaleLoadRequest.SaleOrderDTO> orders;

    /**
     * 是否需要确认
     */
    private boolean saleConfirm;

    /**
     * 确认负责人
     */
    private String owner;

    /**
     * 订单创建门店简码
     */
    private String createShortcodes;

    /**
     * 销售门店id
     */
    private Integer shopId;

    /**
     * 销售类型
     * TO_C_SALE_ON_LINE("toCSaleOnLine", "线上销售"),
     */
    private String saleProcess;

    @Data
    @Accessors(chain = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleOrderDTO implements Serializable {
        /**
         * 单号
         */
        private String serialNo;
        /**
         * 发货位置门店id
         */
        private Integer deliveryLocationId;
        /**
         * 发货位置门店简码
         */
        private String shortcodes;
        /**
         * 出库单列表
         */
        List<StoreWorkDTO> storeWorkList;

    }

    @Data
    @Accessors(chain = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreWorkDTO implements Serializable {
        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 预作业单号
         */
        private String serialNo;
    }
}
