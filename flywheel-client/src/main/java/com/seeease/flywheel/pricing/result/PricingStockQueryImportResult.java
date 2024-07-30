package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 采购
 * @Date create in 2023/3/31 10:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingStockQueryImportResult implements Serializable {

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 单号
     */
    private String serialNo;

    private Integer id;

    private BigDecimal tobPrice;

    private BigDecimal tocPrice;
    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    private BigDecimal consignmentPrice;

}
