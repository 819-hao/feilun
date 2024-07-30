package com.seeease.flywheel.sale.result;

import com.seeease.flywheel.storework.result.StoreWorkCreateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderCreateResult implements Serializable {

    /**
     * 订单集合
     */
    private List<SaleOrderDto> orders;

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

    private BigDecimal totalSalePrice;

    @Data
    @Accessors(chain = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleOrderDto implements Serializable {
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
        List<StoreWorkCreateResult> storeWorkList;

        private Integer id;
    }
}
