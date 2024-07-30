package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 采购类型
 *
 * @author Tiro
 * @date 2023/2/28
 */
@Getter
@AllArgsConstructor
public enum PurchaseTypeEnum implements IEnum<Integer> {

    TH_CG(1, "同行采购"),
    TH_JS(2, "同行寄售"),
    GR_JS(3, "个人寄售"),
    GR_HS(4, "个人回收"),
    GR_HG(5, "个人回购"),
    JT_CG(6, "集团采购"),
    ;
    private Integer value;
    private String desc;

    public static PurchaseTypeEnum fromCode(int value) {
        return Arrays.stream(PurchaseTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }

}