package com.seeease.flywheel.web.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Tiro
 * @date 2023/7/18
 */
@AllArgsConstructor
@Getter
public enum WhetherUseEnum implements IEnum<Integer> {

    USE(1, "已审核"),
    INIT(0, "待审核"),
    CANCEL(2, "已取消"),
    ;
    private Integer value;
    private String desc;

    public static WhetherUseEnum findByValue(Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return Arrays.stream(WhetherUseEnum.values())
                .filter(t -> t.getValue().intValue() == value)
                .findFirst()
                .orElse(null);
    }
}