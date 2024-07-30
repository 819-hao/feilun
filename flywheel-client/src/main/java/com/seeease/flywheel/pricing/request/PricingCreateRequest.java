package com.seeease.flywheel.pricing.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingCreateRequest implements Serializable {

    /**
     * 新建的时候必要条件
     */
    private Integer stockId;

    private String originSerialNo;

    private String storeWorkSerialNo;

    private BigDecimal purchasePrice;

    private Integer pricingSource;

    /**
     * 重新发起定价
     */
    private Integer id;


    /**
     * 单号在每次开启的时候，会自动更新
     */
    private String serialNo;

    /**
     * 重新定价获取新
     */
    private BigDecimal fixPrice;
    /**
     * 重新定价获取新
     */
    private Integer fixDay;

    /**
     * 是否重新定价 false 新建
     */
    private Boolean again = true;

    /**
     * 是否是取消后重新开启
     */
    private Boolean cancel = false;

    /**
     * 异步
     * 非人工创建会导致拿不到用户数据，创建不成功
     */
    private String createdBy;

    private Integer createdId;

    private String updatedBy;

    private Integer updatedId;

    private Integer storeId;

    /**
     * 是否自动生成定价单
     */
    private Boolean auto = false;

    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    private BigDecimal tobPrice;

    private BigDecimal tocPrice;

    /**
     * tob 毛利率
     */
    private BigDecimal bMargin;

    /**
     * toc毛利率
     */
    private BigDecimal cMargin;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

}
