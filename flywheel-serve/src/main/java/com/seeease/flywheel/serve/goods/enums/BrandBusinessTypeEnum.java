package com.seeease.flywheel.serve.goods.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/10/11
 */
@Getter
@AllArgsConstructor
public enum BrandBusinessTypeEnum implements IEnum<Integer> {
    SMALL_WATCH(1, "综合小表"),
    BIG_WATCH(2, "大表"),
    ;
    private Integer value;
    private String desc;
}