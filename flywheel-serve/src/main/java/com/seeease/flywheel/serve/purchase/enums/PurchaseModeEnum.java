package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsModeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialSalesMethodEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 采购方式
 *
 * @author Tiro
 * @date 2023/1/9
 */
@Getter
@AllArgsConstructor
public enum PurchaseModeEnum implements IEnum<Integer> {

    DEPOSIT(1, "订金"),
    PREPARE(2, "备货"),
    BATCH(3, "集采"),
    RECYCLE(4, "仅回收"),
    DISPLACE(5, "置换"),
    OTHER(6, "其他"),
    FULL_PAYMENT(7, "全款"),
    SPECIAL_GRANT_OF_DEPOSIT(8, "定金特批"),
    ;
    private Integer value;
    private String desc;

    public static PurchaseModeEnum fromCode(int value) {
        return Arrays.stream(PurchaseModeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_TYPE_NOT_SUPPORT));
    }

    public static FinancialDocumentsModeEnum convert(int code) {
        switch (fromCode(code)) {
            case DEPOSIT:
                return FinancialDocumentsModeEnum.PURCHASE_DEPOSIT;
            case PREPARE:
                return FinancialDocumentsModeEnum.PURCHASE_PREPARE;
            case BATCH:
                return FinancialDocumentsModeEnum.PURCHASE_BATCH;
            case RECYCLE:
                return FinancialDocumentsModeEnum.PURCHASE_RECYCLE;
            case DISPLACE:
                return FinancialDocumentsModeEnum.PURCHASE_DISPLACE;
            case OTHER:
                return FinancialDocumentsModeEnum.PURCHASE_OTHER;
            case FULL_PAYMENT:
                return FinancialDocumentsModeEnum.FULL_PAYMENT;
            case SPECIAL_GRANT_OF_DEPOSIT:
                return FinancialDocumentsModeEnum.PURCHASE_DEPOSIT_SA;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }

    public static FinancialSalesMethodEnum convert(PurchaseModeEnum code) {
        switch (code) {
            case DEPOSIT:
                return FinancialSalesMethodEnum.PURCHASE_DEPOSIT;
            case PREPARE:
                return FinancialSalesMethodEnum.PURCHASE_PREPARE;
            case BATCH:
                return FinancialSalesMethodEnum.PURCHASE_BATCH;
            case RECYCLE:
                return FinancialSalesMethodEnum.PURCHASE_RECYCLE;
            case DISPLACE:
                return FinancialSalesMethodEnum.PURCHASE_DISPLACE;
            case OTHER:
                return FinancialSalesMethodEnum.PURCHASE_OTHER;
            case FULL_PAYMENT:
                return FinancialSalesMethodEnum.PURCHASE_FUll_AMOUNT;
            case SPECIAL_GRANT_OF_DEPOSIT:
                return FinancialSalesMethodEnum.PURCHASE_DEPOSIT_SA;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }
}
