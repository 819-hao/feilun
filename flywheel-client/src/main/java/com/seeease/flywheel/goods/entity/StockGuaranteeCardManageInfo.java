package com.seeease.flywheel.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/11/20
 */
@Data
public class StockGuaranteeCardManageInfo implements Serializable {

    private Integer id;

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
     * 是否编辑 0否 1是
     */
    private Integer whetherEdit;
    /**
     * 保卡信息
     */
    private String cardInfo;
    /**
     * 成本
     */
    private BigDecimal cost;
    /**
     * 调拨单号
     */
    private String allocateNo;
    /**
     * 调入方
     */
    private String toName;

    /**
     * 调拨状态：0-未调拨，1-已调拨
     */
    private Integer allocateState;

    /**
     * 调出时间
     */
    private String outTime;

    /**
     * 备注
     */
    private String remarks;
}
