package com.seeease.flywheel.web.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/7/18
 */
@AllArgsConstructor
@Getter
public enum WhetherNotifyEnum implements IEnum<Integer> {

    SUCCESS(1, "成功"),
    INIT(0, "待通知"),
    FAIL(-1, "失败"),
    ;
    private Integer value;
    private String desc;
}
