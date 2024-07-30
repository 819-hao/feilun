package com.seeease.flywheel.serve.purchase.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/2/6
 */
@Data
public class PurchaseLineDetailsVO implements Serializable {


    private Integer id;
    /**
     * 详情id
     */
    private Integer lineId;

    private String serialNo;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;

    /**
     * 创建人
     */
    private String createdBy;

    private Integer stockId;
    private Integer demanderStoreId;
    private Integer customerId;
    private Integer storeId;
    private Integer purchaseSubjectId;
    private Integer customerContactId;
    private Integer purchaseMode;
    private Integer purchaseSource;

}
