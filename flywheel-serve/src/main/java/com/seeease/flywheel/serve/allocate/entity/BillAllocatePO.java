package com.seeease.flywheel.serve.allocate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillAllocatePO implements Serializable {
    /**
     * 调拨单号
     */
    private String serialNo;
    /**
     * 库存id
     */
    private Integer stockId;
    /**
     * 调入方
     */
    private Integer toId;
}
