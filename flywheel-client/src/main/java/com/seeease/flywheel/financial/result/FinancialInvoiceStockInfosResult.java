package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceStockInfosResult implements Serializable {

    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 关联单号
     */
    private String originSerialNo;

    /**
     * 金额
     */
    private BigDecimal originPrice;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 开票id
     */
    private Integer financialInvoiceId;

    /**
     *
     */
    private Integer forwardFiId;

    /**
     * 正向开票单号
     */
    private String forwardSerialNo;

    /**
     * 商品编码
     */
    private String wno;
    /**
     * 主图
     */
    private String image;
    /**
     * 附件
     */
    private String attachment;

    /**
     * 经营权
     */
    private Integer rightOfManagement;
    private String rightOfManagementName;

    /**
     * 商品位置
     */
    private Integer locationId;
    private String locationName;
}
