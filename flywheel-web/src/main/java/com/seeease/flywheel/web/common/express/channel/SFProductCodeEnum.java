package com.seeease.flywheel.web.common.express.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 顺丰产品编码
 * https://open.sf-express.com/developSupport/734349?activeIndex=324604
 *
 * @author Tiro
 * @date 2023/9/19
 */
@AllArgsConstructor
@Getter
public enum SFProductCodeEnum {
    T4("1", "顺丰特快"),
    T6("2", "顺丰标快"),
    ;
    private String value;
    private String desc;
}
