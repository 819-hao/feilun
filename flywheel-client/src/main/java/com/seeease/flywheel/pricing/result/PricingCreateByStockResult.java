package com.seeease.flywheel.pricing.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingCreateByStockResult implements Serializable {

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
}
