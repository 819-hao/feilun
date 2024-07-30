package com.seeease.flywheel.serve.anomaly.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateStateEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/4/12 20:10
 */
@Getter
@AllArgsConstructor
public enum AnomalyStateEnum implements IEnum<Integer> {

    OUT_STOCK(2, "异常待出库"),
    CANCEL_WHOLE(3, "异常质检中"),
    COMPLETE(4, "异常维修中"),
    IN(5, "异常已入库"),
    ;

    private Integer value;
    private String desc;

    public static AllocateStateEnum fromCode(int value) {
        return Arrays.stream(AllocateStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
