package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2024/2/22 10:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnToTimeoutResult implements Serializable {

    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 超时文案
     */
    private String timeoutMsg;

    /**
     * @ 人
     */
    private List<String> msgManList;

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
}
