package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
public class PurchasePlanDetailsRequest implements Serializable {
    /**
     * 采购单id
     */
    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;
}
