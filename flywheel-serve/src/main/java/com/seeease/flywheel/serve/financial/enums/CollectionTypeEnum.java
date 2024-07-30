package com.seeease.flywheel.serve.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CollectionTypeEnum {

    KH_CZ(0, "客户充值"),
    XF_TK(1, "消费收款"),
    CG_TK(2, "采购退款"),
    ;

    private Integer value;
    private String desc;
}
