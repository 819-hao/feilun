package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/3/6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateListResult implements Serializable {
    /**
     * 调拨单id
     */
    private Integer id;
    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private Integer allocateType;

    /**
     * 调拨状态
     */
    private Integer allocateState;

    /**
     * 调拨来源
     */
    private Integer allocateSource;

    /**
     * 调出方
     */
    private String fromName;

    /**
     * 调入方
     */
    private String toName;

    /**
     * 总成本
     */
    private BigDecimal totalCostPrice;

    /**
     * 总寄售价
     */
    private BigDecimal totalConsignmentPrice;

    /**
     * 数量
     */
    private Integer totalNumber;

    /**
     * 采购备注
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
}
