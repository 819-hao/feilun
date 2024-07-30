package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 销售订单确认
 *
 * @author Tiro
 * @date 2023/3/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderConfirmRequest implements Serializable {

    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 第一销售人
     */
    private Integer firstSalesman;

    /**
     * 第二销售人
     */
    private Integer secondSalesman;

    /**
     * 第三销售人
     */
    private Integer thirdSalesman;
    /**
     * 确认订单行
     */
    private List<SaleOrderConfirmLineDto> details;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 第三方单号
     */
    private String bizOrderCode;
    /**
     * 客户id
     */
    private Integer customerId;
    /**
     * 客户联系id
     */
    private Integer customerContactsId;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleOrderConfirmLineDto implements Serializable {
        /**
         * 订单行id
         */
        private Integer id;
        /**
         * 库存id
         */
        private Integer stockId;

        /**
         * 表带类型
         */
        private String strapMaterial;

        /**
         * 表节
         */
        private String watchSection;

        /**
         * 是否收取表带更换费 1:是
         */
        private Integer whetherFix;

        /**
         * 是否签回购协议 1:是
         */
        private Integer isCounterPurchase;

        /**
         * 备注
         */
        private String remarks;
    }
}
