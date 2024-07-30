package com.seeease.flywheel.pricing.result;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class WuyuPricingPageResult implements Serializable {
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
     * 新物鱼兜底价
     */
    private BigDecimal newWuyuBuyBackPrice;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 创建时间
     */
    private String createdTime;
}
