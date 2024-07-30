package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;




@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDemandConfirmRequest implements Serializable {

    private Integer id;
    /**
     * 需求成色
     */
    private String fineness;
    /**
     * 需求附件
     */
    private String attachment;
    /**
     * 预计销售价
     */
    private BigDecimal sellPrice;
    /**
     * 需求门店id
     */
    private Integer shopId;
    /**
     * 型号id
     */
    private Integer goodsWatchId;
}
