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
public enum FinancialSalesMethodEnum implements IEnum<Integer> {
    /*销售方式*/
    SALE_NORMAL(1, "销售-正常"),
    SALE_DEPOSIT(2, "销售-订金"),
    SALE_PRESENTED(3, "销售-赠送"),
    SALE_CONSIGN_FOR_SALE(4, "销售-寄售"),
    SALE_ON_LINE(5, "销售-平台"),
    /*采购方式*/
    PURCHASE_DEPOSIT(6, "采购-定金"),
    PURCHASE_PREPARE(7, "采购-备货"),
    PURCHASE_BATCH(8, "采购-批量"),
    PURCHASE_RECYCLE(9, "采购-仅回收"),
    PURCHASE_DISPLACE(10, "采购-置换"),
    PURCHASE_OTHER(11, "采购-其他"),

    REFUND(12, "退货"),
    CZ(13, "充值"),
    SALE_RETURN_POINT(14, "销售-返点"),

    PURCHASE_FUll_AMOUNT(15, "采购-全款"),
    PURCHASE_DEPOSIT_SA(16, "采购-定金特批"),
    FULL_PAYMENT(17, "全款"),
    FINAL_PAYMENT(18, "尾款"),
    PURCHASE_C(19, "采购-寄售"),
    PURCHASE_D(20, "采购-差额"),
    PURCHASE_MARGIN_COVER(21, "退差价"),
    PURCHASE_CANCEL(22, "采购-取消采购计划"),
    ACCOUNT_BALANCE(23, "正常余额"),
    JS_AMOUNT(24, "寄售保证金"),
    ;
    private Integer value;
    private String desc;

    public static FinancialSalesMethodEnum fromCode(int value) {
        return Arrays.stream(FinancialSalesMethodEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
