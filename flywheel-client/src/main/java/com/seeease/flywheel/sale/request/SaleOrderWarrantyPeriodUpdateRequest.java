package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/4/13
 */
@Data
public class SaleOrderWarrantyPeriodUpdateRequest implements Serializable {

    private String serialNo;

    private Integer stockId;
}
