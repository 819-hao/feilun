package com.seeease.flywheel.serve.financial.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 订单类型
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum FinancialClassificationEnum implements IEnum<Integer> {
    TH_CG(1, "同行采购"),
    TH_JS(2, "同行寄售"),
    GR_HS(3, "个人回收"),
    GR_HG(4, "个人回购"),
    GR_JS(5, "个人寄售"),
    CG_TH(6, "采购退货"),
    GR_XS(7, "个人销售"),
    TH_XS(8, "同行销售"),
    XS_TH(9, "销售退货"),
    ;
    private Integer value;
    private String desc;

    public static FinancialClassificationEnum fromCode(int value) {
        return Arrays.stream(FinancialClassificationEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
