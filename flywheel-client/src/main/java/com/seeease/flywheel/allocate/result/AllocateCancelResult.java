package com.seeease.flywheel.allocate.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateCancelResult implements Serializable {
    /**
     * 调拨单id
     */
    private Integer id;

    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 订单行商品
     */
    private List<Integer> stockIdList;
}
