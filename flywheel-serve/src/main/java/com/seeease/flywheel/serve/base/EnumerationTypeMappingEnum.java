package com.seeease.flywheel.serve.base;

import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 枚举正向逆向关系关联
 * @author wbh
 * @date 2023/2/1
 */
@Getter
@AllArgsConstructor
public enum EnumerationTypeMappingEnum {

    GR_HG_JHS_TO_TH(BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_JHS_TH),
    GR_HG_ZH_TO_TH(BusinessBillTypeEnum.GR_HG_ZH, BusinessBillTypeEnum.GR_HG_ZH_TH),
    ;
    private BusinessBillTypeEnum forward;
    private BusinessBillTypeEnum backward;

    public static EnumerationTypeMappingEnum fromCode(BusinessBillTypeEnum businessBillTypeEnum) {
        return Arrays.stream(EnumerationTypeMappingEnum.values())
                .filter(t -> t.getForward().equals(businessBillTypeEnum))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.PURCHASE_RETURN_TYPE_NOT_SUPPORT));
    }
}
