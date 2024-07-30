package com.seeease.flywheel.sale.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author trio
 * @date 2023/1/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReturnOrderListResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 类型
     */
    private Integer saleReturnType;

    /**
     * 数量
     */
    private Integer saleReturnNumber;

    /**
     * 方式
     */
    private Integer saleMode;

    /**
     * 单号
     */
    private String serialNo;
    private String parentSerialNo;
    private String saleSerialNo;

    /**
     * 联系人
     */
    private String customerName;


    /**
     * 状态
     */
    private Integer saleReturnState;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    private String finishTime;
}
