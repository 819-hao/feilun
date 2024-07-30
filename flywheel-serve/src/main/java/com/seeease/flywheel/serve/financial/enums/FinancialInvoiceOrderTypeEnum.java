package com.seeease.flywheel.serve.financial.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum FinancialInvoiceOrderTypeEnum implements IEnum<Integer> {
    GR_XS(1, "个人销售"),
    TH_XS(2, "同行销售"),
    GR_XS_TH(3, "个人销售退货"),
    TH_XS_TH(4, "同行销售退货"),
    ;
    private Integer value;
    private String desc;

    public static FinancialInvoiceOrderTypeEnum fromCode(int value) {
        return Arrays.stream(FinancialInvoiceOrderTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
