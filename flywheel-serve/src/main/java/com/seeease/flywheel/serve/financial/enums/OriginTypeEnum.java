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
public enum OriginTypeEnum implements IEnum<Integer> {
    CG(1, "采购"),
    CG_TH(2, "采购退货"),
    XS(3, "销售"),
    XS_TH(4, "销售退货"),


    ;
    private Integer value;
    private String desc;

    public static OriginTypeEnum fromCode(int value) {
        return Arrays.stream(OriginTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

}
