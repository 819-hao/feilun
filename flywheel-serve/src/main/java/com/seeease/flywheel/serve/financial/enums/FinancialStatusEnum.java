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
public enum FinancialStatusEnum implements IEnum<Integer> {
    PENDING_REVIEW(1, "待核销"),
    RETURN_PENDING_REVIEW(2, "退货待核销"),
    IN_REVIEW(3, "核销中"),
    AUDITED(4, "已核销"),
    PORTION_WAIT_AUDIT(5, "部分待核销"),
    ;
    private Integer value;
    private String desc;

    public static FinancialStatusEnum fromCode(int value) {
        return Arrays.stream(FinancialStatusEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
