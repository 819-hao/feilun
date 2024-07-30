package com.seeease.flywheel.serve.purchase.enums;

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
public enum TaskPaymentModeEnum implements IEnum<Integer> {
    WECHAT(1, "微信"),
    DOU_YIN(2, "抖音"),
    ALIPAY(3, "支付宝"),
    PAY_BY_CARD(4, "刷卡"),
    CASH_PAYMENT(5, "线下"),
    ;
    private Integer value;
    private String desc;

    public static TaskPaymentModeEnum fromCode(int value) {
        return Arrays.stream(TaskPaymentModeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
