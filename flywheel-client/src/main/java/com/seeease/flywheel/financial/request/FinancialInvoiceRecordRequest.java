package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;


/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class FinancialInvoiceRecordRequest extends PageRequest {

    private Integer financialInvoiceId;

}
