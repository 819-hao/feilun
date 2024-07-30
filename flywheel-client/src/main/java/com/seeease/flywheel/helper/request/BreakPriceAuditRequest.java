package com.seeease.flywheel.helper.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class   BreakPriceAuditRequest implements Serializable {
    private List<Integer> ids;
    /**
     * 审核状态
     */
    private Integer status;
    /**
     * 驳回原因
     */
    private String failReason;
  
}
