package com.seeease.flywheel.serve.purchase.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseDemandStatusEnum implements IEnum<Integer> {
    /**
     * 待确认
     */
    WAIT(1),
    /**
     * 确认完成
     */
    OK(2),
    /**
     * 取消
     */
    CANCEL(3)
    ;
    private Integer value;
}
