package com.seeease.flywheel.serve.account.enums;

import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.seeeaseframework.mybatis.transitionstate.IStateEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/18 17:08
 */
@Getter
@AllArgsConstructor
public enum CompanyTypeEnum implements IStateEnum<Integer> {

    INIT(1, "工资"),
    QT_ING(2, "社保"),
    DELIVERED(3, "公积金"),
    IN_STOCK(4, "提成"),
    RETURNING(5, "绩效"),
    RETURNED(6, "场地租金"),
    CANCEL(7, "场地物业费"),
    CANCEL1(8, "费用摊销"),
    CANCEL2(9, "折旧"),
    CANCEL3(10, "差旅费"),
    CANCEL4(11, "交通费"),
    CANCEL5(12, "备用金"),
    CANCEL6(13, "付款单"),
    ;
    private Integer value;
    private String desc;

    public static CompanyTypeEnum fromCode(String desc) {
        return Arrays.stream(CompanyTypeEnum.values())
                .filter(t -> desc.equals(t.getDesc()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    public static CompanyTypeEnum fromCode(int value) {
        return Arrays.stream(CompanyTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }
}
