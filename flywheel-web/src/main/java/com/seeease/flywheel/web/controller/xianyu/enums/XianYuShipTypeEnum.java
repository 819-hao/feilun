package com.seeease.flywheel.web.controller.xianyu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支持当前地区的交付类型 1:顺丰邮寄，2：上门;3：到店
 *
 * @author Tiro
 * @date 2023/10/17
 */
@Getter
@AllArgsConstructor
public enum XianYuShipTypeEnum {

    /**
     * 1:顺丰邮寄
     */
    SF(1L),
    /**
     * 2：上门
     */
    PICKUP(2L),
    /**
     * 3：到店
     */
    SHOP(3L),
    ;
    private long value;
}
