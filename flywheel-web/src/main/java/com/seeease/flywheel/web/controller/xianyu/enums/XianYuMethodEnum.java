package com.seeease.flywheel.web.controller.xianyu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Getter
@AllArgsConstructor
public enum XianYuMethodEnum {
    UNDEFINED("未定义"),
    /**
     * 获取回收商的报价模板
     */
    RECYCLE_QUOTE_TEMPLATE("qimen.alibaba.idle.recycle.quote.template"),
    /**
     * 闲鱼请求服务商估价（使用闲鱼自建问卷）
     */
    RECYCLE_QUOTE_GET("qimen.alibaba.idle.template.recycle.quote.get"),
    /**
     * 闲鱼请求服务商估价（使用服务商问卷）
     */
    RECYCLE_QUOTE_GET_2("qimen.alibaba.idle.recycle.quote.get"),
    /**
     * 闲鱼请求服务商信用预付检验
     */
    RECYCLE_ORDER_PREPAY_CHECK("qimen.alibaba.idle.recycle.order.prepay.check"),
    /**
     * 闲鱼请求服务商上门地址校验
     */
    RECYCLE_ADDRESS_CHECK("qimen.alibaba.idle.recycle.address.check"),

    ;

    private String method;

    public static XianYuMethodEnum fromMethod(String method) {
        return Arrays.stream(XianYuMethodEnum.values())
                .filter(t -> method.equals(t.getMethod()))
                .findFirst()
                .orElse(UNDEFINED);
    }
}
