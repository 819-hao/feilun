package com.seeease.flywheel.serve.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum FinancialDocumentsTypeEnum {

    /**
     * 采购
     */
    CG(100),
    /**
     * 采购退货
     */
    CG_TH(200),
    /**
     * 销售
     */
    XS(300),
    /**
     * 销售退货
     */
    XS_TH(400),
    /**
     * 商品调拨
     */
    SP_DB(500),
    /**
     * 服务费
     */
    FWF(600),
    ;


    private final int value;

    public static FinancialDocumentsTypeEnum getByCode(int code) {
        return Arrays.stream(FinancialDocumentsTypeEnum.values()).filter(t -> t.getValue() == code)
                .findFirst().orElse(null);
    }
}
