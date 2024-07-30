package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInvoiceReverseCancelRequest implements Serializable {

    /**
     *
     */
    private List<Integer> ids;

}
