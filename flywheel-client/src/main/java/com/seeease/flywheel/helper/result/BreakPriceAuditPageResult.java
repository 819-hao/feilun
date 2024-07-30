package com.seeease.flywheel.helper.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreakPriceAuditPageResult implements Serializable {
    private Integer id;

    /**
     * 原因
     */
    private String reason;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 更新人
     */
    private String updatedBy;
    /**
     * 更新时间
     */
    private String updatedTime;
    /***
     * 创建时间
     */
    private String createdTime;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 系列名称
     */
    private String seriesName;
    /**
     * 型号
     */
    private String model;
    /**
     * 表身号
     */
    private String sn;
    /**
     * 分级
     */
    private String level;
    /**
     * 成交价
     */
    private String clinchPrice;
    /**
     * 原始b价格
     */
    private String tobPrice;
    /**
     * 原始c价格
     */
    private String tocPrice;
    /**
     * 驳回原因
     */
    private String failReason;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 编号
     */
    private String serial;
    /**
     * 商品id
     */
    private String stockId;
  
}
