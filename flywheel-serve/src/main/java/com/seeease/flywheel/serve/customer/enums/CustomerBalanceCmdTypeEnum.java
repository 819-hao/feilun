package com.seeease.flywheel.serve.customer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerBalanceCmdTypeEnum {

    ADD(1, "正向流水，新增"),
    MINUS(-1, "负向流水，扣减"),
    ;
    private Integer value;
    private String desc;
}
