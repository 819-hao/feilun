package com.seeease.flywheel.pricing.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/11/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesPriorityModifyImportResult implements Serializable {
    /**
     * 品牌
     */
    private String brandName;

    /**
     * 型号
     */
    private String model;

    /**
     * 影响商品数量
     */
    private Integer rowsGoods;
}
