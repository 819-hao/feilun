package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/2/10 15:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkPrintLabelResult implements Serializable {

    private String stockSn;

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
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 创建人
     */
    private String createdBy;

    private Integer purchaseType;

    private Integer purchaseSource;
}
