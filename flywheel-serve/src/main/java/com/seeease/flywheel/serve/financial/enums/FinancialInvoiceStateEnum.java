package com.seeease.flywheel.serve.financial.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author wbh
 * @date 2023/3/6
 */
@Getter
@AllArgsConstructor
public enum FinancialInvoiceStateEnum implements IEnum<Integer> {
    //0 、 2 、 3 用于 销售单状态
    // 1 、 3 、4 、 5 用于发票
    NO_INVOICED(0, "未开票"),
    PENDING_INVOICED(1, "待开票"),
    IN_INVOICE(2, "开票中"),
    INVOICE_COMPLETE(3, "已开票"),
    REJECTED(4, "已驳回"),
    CANCELED(5, "已取消"),
    ;
    private Integer value;
    private String desc;

    public static FinancialInvoiceStateEnum fromCode(int value) {
        return Arrays.stream(FinancialInvoiceStateEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
