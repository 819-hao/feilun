package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class SaleReturnOrderDetailsRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 单号
     */
    private String serialNo;
}
