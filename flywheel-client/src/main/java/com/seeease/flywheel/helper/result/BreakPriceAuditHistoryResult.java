package com.seeease.flywheel.helper.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class BreakPriceAuditHistoryResult implements Serializable {


    /**
     * 驳回原因
     */
    private String failReason;
    /**
     * 状态
     */
    private Integer changeStatus;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 创建时间
     */
    private String createdTime;
}
