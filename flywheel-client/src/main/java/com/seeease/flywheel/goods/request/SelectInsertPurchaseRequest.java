package com.seeease.flywheel.goods.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/10/10 10:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectInsertPurchaseRequest implements Serializable {

    private Integer id;

    private String serialNo;

    private BigDecimal totalPurchasePrice;

}
