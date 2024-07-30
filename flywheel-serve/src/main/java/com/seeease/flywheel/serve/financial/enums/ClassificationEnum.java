package com.seeease.flywheel.serve.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClassificationEnum {

    CGTH(0, "采购退货"),
    XS(1, "销售"),
    ;

    private Integer value;
    private String desc;
}
