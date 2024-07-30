package com.seeease.flywheel.serve.sale.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsModeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
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
public enum SaleOrderModeEnum implements IEnum<Integer> {
    NORMAL(1, "正常"),
    DEPOSIT(2, "订金"),
    PRESENTED(3, "赠送"),
    CONSIGN_FOR_SALE(4, "寄售"),
    ON_LINE(5, "平台"),
    RETURN_POINT(6, "返点"),
    ;
    private Integer value;
    private String desc;

    public static SaleOrderModeEnum fromCode(int value) {
        return Arrays.stream(SaleOrderModeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    public static FinancialDocumentsModeEnum convert(int code) {
        switch (fromCode(code)) {
            case NORMAL:
                return FinancialDocumentsModeEnum.SALE_NORMAL;
            case DEPOSIT:
                return FinancialDocumentsModeEnum.SALE_DEPOSIT;
            case PRESENTED:
                return FinancialDocumentsModeEnum.SALE_PRESENTED;
            case CONSIGN_FOR_SALE:
                return FinancialDocumentsModeEnum.SALE_CONSIGN_FOR_SALE;
            case ON_LINE:
                return FinancialDocumentsModeEnum.SALE_ON_LINE;
            case RETURN_POINT:
                return FinancialDocumentsModeEnum.RETURN_POINT;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }

    public static FinancialSalesMethodEnum convert(SaleOrderModeEnum code) {
        switch (code) {
            case DEPOSIT:
                return FinancialSalesMethodEnum.SALE_DEPOSIT;
            case NORMAL:
                return FinancialSalesMethodEnum.SALE_NORMAL;
            case PRESENTED:
                return FinancialSalesMethodEnum.SALE_PRESENTED;
            case CONSIGN_FOR_SALE:
                return FinancialSalesMethodEnum.SALE_CONSIGN_FOR_SALE;
            case ON_LINE:
                return FinancialSalesMethodEnum.SALE_ON_LINE;
            case RETURN_POINT:
                return FinancialSalesMethodEnum.SALE_RETURN_POINT;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }
}
