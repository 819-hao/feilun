package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/5/11
 */
@Data
public class FinancialStatementBatchAuditRequest implements Serializable {
    private List<Integer> ids;
    /**
     * 审核说明
     */
    private String auditDescription;
}
