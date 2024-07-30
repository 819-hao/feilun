package com.seeease.flywheel.serve.goods.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/5/6
 */
@Getter
@AllArgsConstructor
public enum SeriesTypeEnum implements IEnum<Integer> {
    WRISTWATCH(0, "腕表"),
    BAGS(1, "箱包"),
    ORNAMENT(2, "饰品"),
    ACCESSORY(3, "配件"),
    ;
    private Integer value;
    private String desc;

    public static SeriesTypeEnum fromValue(int value) {
        return Arrays.stream(SeriesTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
