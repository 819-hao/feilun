package com.seeease.flywheel.serve.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/11/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesPriorityModifyDTO implements Serializable {

    /**
     * 商品型号id
     */
    private Integer goodsId;

    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    /**
     * 在库商品状态
     */
    private List<Integer> stockStatusList;

    /**
     * 修改人id
     */
    private Integer updatedId;

    /**
     * 修改人
     */
    private String updatedBy;
}
