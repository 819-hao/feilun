package com.seeease.flywheel.serve.fix.enums;

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
public enum FlowGradeEnum implements IStateEnum<Integer> {

    UNDEFINED(0, "未定义"),
    CREATE(1, "一级（个人寄售）"),
    RECEIVE(2, "二级（同行采购-定金）"),
    NORMAL(3, "三级（同行采购-备货"),
    ANOMALY(4, "四级（同行采购-集采）"),
    ANOMALY2(5, "五级（同行寄售）"),
    EXTERNAL(6, "六级（个人回收）"),
    EXTERNAL2(7, "七级（个人回购）"),
    ;
    private Integer value;
    private String desc;


    public static FlowGradeEnum fromCode(int value) {
        return Arrays.stream(FlowGradeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
