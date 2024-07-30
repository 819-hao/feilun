package com.seeease.flywheel.serve.storework.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/10/8
 */
@Getter
@AllArgsConstructor
public enum WmsWorkPrintExpressState implements IEnum<Integer> {
    INIT(0, "待打单"),
    SYSTEM(1, "系统已打单"),
    MANUAL(2, "人工已录入"),
    ;
    private Integer value;
    private String desc;
}