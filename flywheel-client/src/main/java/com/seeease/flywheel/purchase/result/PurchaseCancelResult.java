package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCancelResult implements Serializable {
    /**
     * 采购单号
     */
    private String serialNo;

    private List<PurchaseDetailsResult.PurchaseLineVO> line;
}
