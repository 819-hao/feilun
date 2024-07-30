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
public class SaleOrderListResult implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 类型
     */
    private Integer saleType;

    /**
     * 数量
     */
    private Integer saleNumber;

    /**
     * 方式
     */
    private Integer saleMode;

    /**
     * 单号
     */
    private String serialNo;
    private String parentSerialNo;

    /**
     * 联系人
     */
    private String customerName;

    /**
     * 总成交价
     */
    private BigDecimal totalSalePrice;

    /**
     * 状态
     */
    private Integer saleState;

    /**
     * 快递单号
     */
    private String expressNumber;

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

    private Integer saleTime;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    /**
     * 是否有退货
     */
    private boolean whetherTH;

    /**
     * 销售渠道
     */
    private Integer saleChannel;
}
