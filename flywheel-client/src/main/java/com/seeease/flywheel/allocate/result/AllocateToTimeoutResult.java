package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * 商品调拨接收方超时
 * @date 2023/3/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateToTimeoutResult implements Serializable {

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
