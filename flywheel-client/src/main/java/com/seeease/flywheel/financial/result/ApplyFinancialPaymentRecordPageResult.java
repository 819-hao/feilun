package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyFinancialPaymentRecordPageResult implements Serializable {


    private Integer applyFinancialPaymentId;

    /**
     * 2 审核驳回
     * 1 审核通过
     * 4 审核作废
     */
    private Integer state;

    /**
     * 拒绝原因
     */
    private String result;

    private String createdBy;

    private String createdTime;

    private String applicant;

    private String applicantTime;

}
