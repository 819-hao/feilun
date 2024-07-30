package com.seeease.flywheel.sale.result;

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
public class SaleReturnOrderCancelResult implements Serializable {
    /**
     * 单号
     */
    private String serialNo;

    private List<SaleReturnOrderDetailsResult.SaleReturnOrderLineVO> line;
}
