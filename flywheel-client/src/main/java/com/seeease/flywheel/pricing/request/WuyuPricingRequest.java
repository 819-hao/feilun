package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class WuyuPricingRequest implements Serializable {

    public List<Item> list ;

    @Data
    public static class Item implements Serializable{
        /**
         * id
         */
        private Integer id;
        /**
         * 表身号
         */
        private String stockSn;
        /**
         * 原因
         */
        private String reason;
        /**
         * 采购价
         */
        private BigDecimal purchasePrice;
        /**
         * 新采购价
         */
        private BigDecimal newPurchasePrice;
        /**
         * 物鱼供货价
         */
        private BigDecimal wuyuPrice;
        /**
         * 新物鱼供货价
         */
        private BigDecimal newWuyuPrice;
        /**
         * 兜底价
         */
        private BigDecimal wuyuBuyBackPrice;
        /**
         * 新兜底价
         */
        private BigDecimal newWuyuBuyBackPrice;
    }
}
