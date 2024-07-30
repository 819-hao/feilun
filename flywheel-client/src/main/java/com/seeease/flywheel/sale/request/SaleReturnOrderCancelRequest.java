package com.seeease.flywheel.sale.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
public class SaleReturnOrderCancelRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    private Integer saleId;

    private String serialNo;
}
