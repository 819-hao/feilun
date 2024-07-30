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
public enum FinancialDocumentsModeEnum implements IEnum<Integer> {
    /*销售方式*/
    SALE_NORMAL(1, "正常"),
    SALE_DEPOSIT(2, "订金"),
    SALE_PRESENTED(3, "赠送"),
    SALE_CONSIGN_FOR_SALE(4, "寄售"),
    SALE_ON_LINE(5, "平台"),
    /*采购方式*/
    PURCHASE_DEPOSIT(6, "定金"),
    PURCHASE_PREPARE(7, "备货"),
    PURCHASE_BATCH(8, "批量"),
    PURCHASE_RECYCLE(9, "仅回收"),
    PURCHASE_DISPLACE(10, "置换"),
    PURCHASE_OTHER(11, "其他"),

    REFUND(12, "退货"),
    RETURN_POINT(13, "返点"),
    FULL_PAYMENT(15, "全款"),
    PURCHASE_DEPOSIT_SA(16, "定金特批"),
    PURCHASE_MARGIN_COVER(17, "退差价"),
    ;
    private Integer value;
    private String desc;

    public static FinancialDocumentsModeEnum fromCode(int value) {
        return Arrays.stream(FinancialDocumentsModeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
