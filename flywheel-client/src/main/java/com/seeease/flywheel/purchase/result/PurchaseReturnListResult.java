package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnListResult implements Serializable {
    /**
     * 采购退货单id
     */
    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    private Integer number;

    private String createdBy;

    private String createdTime;

    private Integer purchaseReturnState;

    private String remarks;

    private BigDecimal returnPrice;

    private String customerName;

    private Integer customerId;

}
