package com.seeease.flywheel.account.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 16:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostJdFlMappingResult implements Serializable {

    private Integer id;

    /**
     * 飞轮费用归类
     */
    private String flGroup;

    /**
     * 飞轮费用分类
     */
    private String flType;

    /**
     * 金蝶科目编码
     */
    private String jdGroup;

    /**
     * 金蝶核算维度类型
     */
    private String jdType;
}
