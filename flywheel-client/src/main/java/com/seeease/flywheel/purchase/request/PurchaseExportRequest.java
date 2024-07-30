package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
public class PurchaseExportRequest implements Serializable {

    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 老表身号
     */
    private String oldStockSn;
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 采购类型
     */
    private Integer purchaseType;

    /**
     * 采购方式
     */
    private Integer purchaseMode;

    /**
     * 供应商
     */
    private String customerName;

    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 状态
     */
    private Integer purchaseState;

    /**
     * 客户id集合
     */
    private List<Integer> customerIdList;

    /**
     * 门店id
     */
    private Integer storeId;

    private Integer purchaseSubjectId;

    List<Integer> ids;

}
