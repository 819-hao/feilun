package com.seeease.flywheel.serve.financial.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinancialDocumentsOriginEnum {

    /**
     * 同行采购
     */
    TH_CG(100),

    /**
     * 个人回收
     */
    GR_HS(200),

    /**
     * 个人回购
     */
    GR_HG(300),

    /**
     * 回购服务
     */
    HG_FW(400),

    /**
     * 寄售调出
     */
    JS_DC(500),

    /**
     * 寄售调入
     */
    JS_DR(600),

    /**
     * 个人销售
     */
    GR_XS(700),

    /**
     * 同行销售
     */
    TH_XS(800),

    /**
     * 服务费支出
     */
    FW_ZC(900),

    /**
     * 服务费收入
     */
    FW_SR(1000),

    /**
     *  采购折让（新增）
     */
    CG_ZR(1100),
    ;

    private final int value;
}
