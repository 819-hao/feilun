package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description 补差额
 * @Date create in 2023/4/18 10:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseMarginCoverRequest implements Serializable {

    private Integer lineId;

    private BigDecimal finalPurchase;
}
