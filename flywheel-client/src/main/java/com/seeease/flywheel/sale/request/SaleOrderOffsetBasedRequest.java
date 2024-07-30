package com.seeease.flywheel.sale.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleOrderOffsetBasedRequest implements Serializable {
    /**
     * 当前偏移量
     */
    private Integer currentOffset;
    /**
     * 查询分页尺寸
     */
    private Integer limit;
}
