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
public class SaleOrderOffsetBasedResult implements Serializable {
    /**
     * 销售单列表
     */
    private List<SaleOrder> saleOrderList;
    /**
     * 当前偏移量
     */
    private Integer currentOffset;
    /**
     * 是否最后的数据
     */
    private boolean isEnd;
}
