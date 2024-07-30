package com.seeease.flywheel.web.controller.xianyu.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleQuoteGetResult extends QiMenBaseResult {
    /**
     * 问卷 spuId
     */
    private String spuId;
    /**
     * 价格 分
     */
    private Long price;
    /**
     * 估价id
     */
    private String quoteId;
    /**
     * 支持当前地区的交付类型 1:顺丰邮寄，2：上门;3：到店
     */
    private List<Long> shipTypes;
}
