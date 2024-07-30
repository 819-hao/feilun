package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * 申请打款通过通知
 * @author dmmasxnmf
 */
@Data
public class ApplyFinancialPaymentPassMessageRequest extends PageRequest {


    private String processInstanceId;

    private String activityId;
}
