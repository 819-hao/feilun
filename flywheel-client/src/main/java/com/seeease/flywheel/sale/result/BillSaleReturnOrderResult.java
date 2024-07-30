package com.seeease.flywheel.sale.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillSaleReturnOrderResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     *编码
     */
    private String serialNo;


    /**
     * 快递单号
     */
    private String expressNumber;

    private Integer shopId;

}
