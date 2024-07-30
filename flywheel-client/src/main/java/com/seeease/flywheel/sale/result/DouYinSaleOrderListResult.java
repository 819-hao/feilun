package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DouYinSaleOrderListResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    /**
     * 订单来源
     */
    private Integer shopId;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    private List<SaleOrderLineDto> lineList;

    @Data
    public static class SaleOrderLineDto implements Serializable {
        /**
         * 快递单号
         */
        private String expressNumber;

        private String spotCheckCode;

        private String subOrderCode;
    }
}
