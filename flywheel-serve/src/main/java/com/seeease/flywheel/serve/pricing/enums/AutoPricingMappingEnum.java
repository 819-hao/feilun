package com.seeease.flywheel.serve.pricing.enums;

import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.base.ExceptionCode;
import com.seeease.flywheel.serve.base.SeeeaseConstant;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/7/31 14:51
 */
@Getter
@AllArgsConstructor
public enum AutoPricingMappingEnum {

    /**
     * 其他
     */
    TYPE_1_0(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST, Arrays.asList(), "其他", BigDecimal.valueOf(1.03), BigDecimal.valueOf(1.15), BigDecimal.valueOf(60000L), BigDecimal.valueOf(1.12)),
    /**
     * 劳力士
     */
    TYPE_1_1(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST, Arrays.asList(16), "劳力士", BigDecimal.valueOf(1.03), BigDecimal.valueOf(1.07), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.07)),
    /**
     * 江诗丹顿 or 宝珀
     */
    TYPE_1_2(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST, Arrays.asList(13, 2), "江诗丹顿 or 宝珀", BigDecimal.valueOf(1.03), BigDecimal.valueOf(1.12), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.12)),

//    /**
//     * 宝珀
//     */
//    TYPE_1_3(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST, 2, "", BigDecimal.valueOf(1.03), BigDecimal.valueOf(1.12), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.12)),
    /**
     * 万国 or 积家
     */
    TYPE_1_3(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST, Arrays.asList(22, 11), "万国 or 积家", BigDecimal.valueOf(1.03), BigDecimal.valueOf(1.11), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.11)),
//    /**
//     * 积家
//     */
//    TYPE_1_5(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST, 11, "积家", BigDecimal.valueOf(1.03), BigDecimal.valueOf(1.11), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.11)),

    /**
     * 回收
     */
    TYPE_2(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST2, Arrays.asList(), "其他", BigDecimal.valueOf(1.08), BigDecimal.valueOf(1.23), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.23)),
    /**
     * 同行寄售
     */
    TYPE_3(SeeeaseConstant.BUSINESS_BILL_TYPE_ENUM_LIST3, Arrays.asList(), "其他", BigDecimal.valueOf(1.05), BigDecimal.valueOf(1.10), BigDecimal.valueOf(Long.MAX_VALUE), BigDecimal.valueOf(1.10)),


    ;

    /**
     * 采购来源
     */
    private List<BusinessBillTypeEnum> billTypeList;

    /**
     * 品牌id
     */
    private List<Integer> brandIdList;

    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * tob利率
     */
    private BigDecimal toBMargin;

    /**
     * toc利率
     */
    private BigDecimal toCMargin;

    /**
     * toc上限天花板价格
     */
    private BigDecimal toCCeilingPrice;

    /**
     * 额外利率
     */
    private BigDecimal toCOtherMargin;


    public static AutoPricingMappingEnum fromCode(Integer businessBillingType, Integer brandId) {
        List<AutoPricingMappingEnum> collect = Arrays.stream(AutoPricingMappingEnum.values()).filter(r -> r.billTypeList.contains(BusinessBillTypeEnum.fromValue(businessBillingType))).collect(Collectors.toList());
        if (collect.stream().anyMatch(r -> r.brandIdList.contains(brandId))) {
            return collect.stream().filter(r -> r.brandIdList.contains(brandId)).findFirst().orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
        } else {
            return collect.stream().filter(r -> CollectionUtils.isEmpty(r.brandIdList)).findFirst().orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
        }
    }
}
