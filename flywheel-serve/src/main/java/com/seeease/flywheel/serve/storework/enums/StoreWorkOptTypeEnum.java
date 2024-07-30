package com.seeease.flywheel.serve.storework.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/3/14
 */
@Getter
@AllArgsConstructor
public enum StoreWorkOptTypeEnum implements IEnum<Integer> {
    RECEIPT(10, "收货"),
    DELIVERY(20, "发货"),
    RETURN(30, "退回"),
    IN_STORAGE(40, "入库"),
    OUT_STORAGE(50, "出库"),
    ;
    private Integer value;
    private String desc;
}
