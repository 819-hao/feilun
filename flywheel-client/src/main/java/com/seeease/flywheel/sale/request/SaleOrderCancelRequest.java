package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderCancelRequest implements Serializable {
    /**
     * id
     */
    private Integer id;

    private String serialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;
}
