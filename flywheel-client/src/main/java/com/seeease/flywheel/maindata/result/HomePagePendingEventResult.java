package com.seeease.flywheel.maindata.result;

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
public class HomePagePendingEventResult implements Serializable {

    /**
     * 待调拨数量
     */
    private long transferredCount;

    /**
     * 待定价数量
     */
    private long pendingPricingCount;

    /**
     * 待接修数量
     */
    private long pendingRepairCount;

    /**
     * 申请破价数量
     */
    private long applyBreakPriceCount;

    /**
     * 回收分配数量
     */
    private long recycleAllocationCount;
}
