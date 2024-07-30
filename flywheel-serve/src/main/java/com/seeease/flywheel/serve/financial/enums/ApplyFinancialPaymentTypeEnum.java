package com.seeease.flywheel.serve.financial.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 *
 */
@Getter
@AllArgsConstructor
public enum ApplyFinancialPaymentTypeEnum implements IEnum<Integer> {
    PEER_PROCUREMENT(0, "同行采购"),
    PERSONAL_RECYCLING(1, "个人回收"),
    SEND_PERSON(2, "个人寄售"),
    BUY_BACK(3, "个人回购"),
    INDEPENDENT_FINANCIAL_SETTLEMENT(4, "财务自主结算"),
    PEER_CONSIGNMENT(5, "同行寄售"),
    PERSONAL_SALES_RETURNS(6, "个人销售退货"),
    BALANCE_REFUND(7, "余额退款"),
    WRONG_AIRWAY_BILL(8, "错单退款"),
    ;
    private Integer value;
    private String desc;

    public static ApplyFinancialPaymentTypeEnum fromCode(int value) {
        return Arrays.stream(ApplyFinancialPaymentTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
