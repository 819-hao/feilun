package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderDetailsRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;



}
