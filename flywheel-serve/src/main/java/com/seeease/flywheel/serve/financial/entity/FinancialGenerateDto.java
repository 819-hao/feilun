package com.seeease.flywheel.serve.financial.entity;

import lombok.Data;

import java.util.List;

/**
 * @author edy
 * @date 2022/9/22
 */
@Data
public class FinancialGenerateDto {
    private Integer id;

    /**
     * stockId列表
     */
    private List<Integer> stockList;

    /**
     * 类型
     */
    private Integer type;
}
