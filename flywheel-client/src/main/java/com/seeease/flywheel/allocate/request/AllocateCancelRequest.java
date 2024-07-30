package com.seeease.flywheel.allocate.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/3/15
 */
@Data
public class AllocateCancelRequest implements Serializable {
    /**
     * 调拨单id
     */
    private Integer allocateId;
    /**
     * 取消原因
     */
    private String cancelReason;
}
