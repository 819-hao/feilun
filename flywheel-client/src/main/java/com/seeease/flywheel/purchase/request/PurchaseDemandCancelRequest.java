package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDemandCancelRequest implements Serializable {

    /**
     * 主键id
     */
    private Integer id;
    /**
     * 商场的商品编码
     */
    private String serial;

}
