package com.seeease.flywheel.serve.qt.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/1/17 14:41
 */
@Getter
@AllArgsConstructor
public enum QualityTestingConclusionEnum implements IStateEnum<Integer> {

    CREATE(1, "正常入库"),
    FIX(2, "维修"),
    RETURN(3, "退货"),
    ANOMALY(4, "异常入库"),


    //循环维修
//    REPEAT_FIX(5, "重复维修"),
    //质检判断需要等客户同意
    CONFIRM_FIX(6, "待确认维修"),
    CONFIRM_BY_RETURN(7, "待需求方确认留不留"),
    CONFIRM_BY_FIX(8, "待需求方确认修不修"),
    RETURN_FIX(9, "返修"),
    RETURN_STOCK(10, "换货"),

    ;
    private Integer value;
    private String desc;

    public static QualityTestingConclusionEnum fromCode(int value) {
        return Arrays.stream(QualityTestingConclusionEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
