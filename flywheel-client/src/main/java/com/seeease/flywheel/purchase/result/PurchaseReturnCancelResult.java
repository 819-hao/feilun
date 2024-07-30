package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnCancelResult implements Serializable {
    /**
     * 采购退货单号
     */
    private String serialNo;

    private List<PurchaseReturnDetailsResult.PurchaseReturnLineVO> line;
}
