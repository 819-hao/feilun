package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePlanCreateResult implements Serializable {

    private Integer id;

    /**
     * 采购单号
     */
    private String serialNo;

    private List<PurchasePlanDetailsResult.PurchasePlanLineVO> line;
}
