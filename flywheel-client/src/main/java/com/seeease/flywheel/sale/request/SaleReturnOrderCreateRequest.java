package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderCreateRequest implements Serializable {
    /**
     * 父订单编号
     */
    private String parentSerialNo;

    /**
     * 第三方退货单
     */
    private String bizOrderCode;

    /**
     * 类型 1同行 2个人
     */
    private Integer saleReturnType;

    /**
     * 退货来源
     */
    private Integer saleReturnSource;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 原路退回
     */
    private boolean returnByOriginalRoute;

    /**
     * 单据详情
     */
    private List<BillSaleReturnOrderLineDto> details;

    /**
     * 指定的创建人，默认取登陆用户
     */
    private PrescriptiveCreator creator;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillSaleReturnOrderLineDto implements Serializable {

        /**
         * 销售单id
         */
        private Integer saleId;

        /**
         * 确认销售单id
         */
        private Integer saleIdCheck;

        /**
         * 销售行id
         */
        private Integer saleLineId;

        /**
         * 第三方子订单
         */
        private String subOrderCode;

        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 商品id
         */
        private Integer goodsId;

        /**
         * 退货金额
         */
        private BigDecimal returnPrice;

        /**
         * 销售行状态
         */
        private Integer saleLineState;
        /**
         *
         */
        private Integer whetherInvoice;

        /**
         * 销售单经营权
         */
        private Integer rightOfManagement;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptiveCreator implements Serializable {

        /**
         * 创建人id
         */
        private Integer createdId;

        /**
         * 创建人
         */
        private String createdBy;
    }
}
