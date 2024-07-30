package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/6 16:40
 */
@Data
public class SaleOrderEditRequest implements Serializable {

    private Integer id;

    private Integer saleSource;

    /**
     * 销售方式
     */
    private Integer saleMode;

    /**
     * 销售渠道
     */
    private Integer saleChannel;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 备注
     */
    private String remarks;

    /**
     *
     */
    private Integer customerId;

    /**
     *
     */
    private String customerName;

    /**
     *
     */
    private String customerPhone;

    /**
     *
     */
    private String customerAddress;

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
     * 订金金额
     */
    private BigDecimal deposit;

    /**
     * 付款方式
     */
    private Integer paymentMethod;

    /**
     * 购买原因
     */
    private Integer buyCause;

    /**
     * 单据详情
     */
    private List<BillSaleOrderLineDto> details;


    @Data
    public static class BillSaleOrderLineDto implements Serializable {

        private Integer id;

        private BigDecimal clinchPrice;

        private String strapMaterial;

        private String watchSection;

        private BigDecimal strapReplacementPrice;

        private Integer isCounterPurchase;

        private Integer isRepurchasePolicy;

        private String buyBackPolicy;

        private String repurchasePolicyUrl;

        private BigDecimal preClinchPrice;
    }
}
