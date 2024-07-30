package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderCreateResult implements Serializable {
    /**
     * 退货订单
     */
    private List<SaleReturnOrderDto> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleReturnOrderDto implements Serializable {
        private Integer returnId;
        private String serialNo;
        private Integer deliveryLocationId;
        private String shortcodes;
        /**
         * 退货商品
         */
        private List<Integer> stockIdList;

    }

}
