package com.seeease.flywheel.serve.allocate.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
 *
 * @author Tiro
 * @date 2023/3/7
 */
@Getter
@AllArgsConstructor
public enum AllocateTypeEnum implements IEnum<Integer> {

    CONSIGN(1, "寄售"),
    CONSIGN_RETURN(2, "寄售归还"),
    FLAT(3, "平调"),
    BORROW(4, "借调"),
    ;
    private Integer value;
    private String desc;

    public static AllocateTypeEnum fromCode(int value) {
        return Arrays.stream(AllocateTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}