package com.seeease.flywheel.financial.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wbh
 * @date 2023/4/27
 */
@Data
public class FinancialGenerateOrderRequest implements Serializable {
    Integer saleType;
    Integer id;
    List<Integer> stockList;
    Integer type;
}
