package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/5/11
 */
@Data
public class FinancialStatementMatchingWriteOffRequest implements Serializable {
    private List<Integer> ids;
}
