
package com.seeease.flywheel.sale.result;

import com.seeease.flywheel.sale.entity.SaleOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderQueryResult implements Serializable {
    /**
     * 销售单列表
     */
    private List<SaleOrder> saleOrderList;
}
