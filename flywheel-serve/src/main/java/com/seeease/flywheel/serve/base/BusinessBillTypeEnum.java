package com.seeease.flywheel.serve.base;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.google.common.collect.ImmutableSet;
import com.seeease.flywheel.serve.financial.enums.FinancialClassificationEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsModeEnum;
import com.seeease.flywheel.serve.financial.enums.FinancialDocumentsOriginEnum;
import com.seeease.springframework.exception.e.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

/**
 * 业务单据类型
 *
 * @author Tiro
 * @date 2023/2/3
 */
@Getter
@AllArgsConstructor
public enum BusinessBillTypeEnum implements IEnum<Integer> {

    TH_CG_DJ(101, "同行采购-订金"),
    TH_CG_BH(102, "同行采购-备货"),
    TH_CG_PL(103, "同行采购-集采"),
    TH_JS(104, "同行寄售"),
    GR_JS(105, "个人寄售"),
    GR_HS_JHS(106, "个人回收-仅回收"),
    GR_HS_ZH(107, "个人回收-置换"),
    GR_HG_JHS(108, "个人回购-回收"),
    GR_HG_ZH(109, "个人回购-置换"),
    TH_CG_QK(110, "同行采购-全款"),
    TH_CG_DJTP(111, "同行采购-定金特批"),

    JT_CG_DJ(121, "集团采购-订金"),
    JT_CG_BH(122, "集团采购-备货"),
    JT_CG_PL(123, "集团采购-集采"),

    GR_HS_JHS_TH(156, "个人回收-回收-退货"),
    GR_HS_ZH_TH(157, "个人回收-置换-退货"),
    GR_HG_JHS_TH(158, "个人回购-回收-退货"),
    GR_HG_ZH_TH(159, "个人回购-置换-退货"),

    ZB_DB(201, "总部调拨"),
    MD_DB(202, "门店调拨"),
    MD_DB_ZB(203, "门店调回总部"),

    MD_WX(112, "门店维修"),

    TO_C_XS(301, "toc销售"),
    TO_B_XS(302, "tob销售-正常"),
    TO_B_JS(303, "tob销售-寄售"),
    TO_C_XS_TH(304, "toc销售退货"),
    TO_B_XS_TH(305, "tob销售退货"),
    TO_C_ON_LINE(306, "线上销售"),
    TO_C_ON_LINE_TH(307, "线上销售退货"),

    CG_TH(401, "采购退货"),
    YC_CL(501, "异常处理"),
    ;

    private Integer value;
    private String desc;
    private static final Set<Integer> POSTF_PURCHASE_TYPE = ImmutableSet.of(
            BusinessBillTypeEnum.TH_CG_BH.getValue(),
            BusinessBillTypeEnum.TH_CG_DJ.getValue(),
            BusinessBillTypeEnum.TH_CG_PL.getValue(),
            BusinessBillTypeEnum.TH_CG_QK.getValue(),
            BusinessBillTypeEnum.TH_CG_DJTP.getValue(),
            BusinessBillTypeEnum.TH_JS.getValue(),
            BusinessBillTypeEnum.GR_JS.getValue(),
            BusinessBillTypeEnum.GR_HS_JHS.getValue(),
            BusinessBillTypeEnum.GR_HS_ZH.getValue(),
            BusinessBillTypeEnum.GR_HG_ZH.getValue(),
            BusinessBillTypeEnum.GR_HG_JHS.getValue()
    );
    //todo
    private static final Set<Integer> POSTF_PURCHASE_RETURN_TYPE = ImmutableSet.of(
            BusinessBillTypeEnum.CG_TH.getValue()
    );

    public static BusinessBillTypeEnum fromValue(int value) {
        return Arrays.stream(BusinessBillTypeEnum.values())
                .filter(t -> value == t.getValue())
                .findFirst()
                .orElseThrow(() -> new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT));
    }

    public static String analysisValue(int value) {
        if (POSTF_PURCHASE_TYPE.contains(value))
            return SeeeaseConstant.CG;
        else if (POSTF_PURCHASE_RETURN_TYPE.contains(value))
            return SeeeaseConstant.CGTH;
        return null;
    }

    public static FinancialDocumentsModeEnum convertMode(int code) {
        switch (fromValue(code)) {
            case TH_CG_DJ:
                return FinancialDocumentsModeEnum.PURCHASE_DEPOSIT;
            case TH_CG_BH:
                return FinancialDocumentsModeEnum.PURCHASE_PREPARE;
            case TH_CG_PL:
                return FinancialDocumentsModeEnum.PURCHASE_BATCH;
            case TH_JS:
            case GR_JS:
                return FinancialDocumentsModeEnum.PURCHASE_OTHER;
            case TH_CG_QK:
                return FinancialDocumentsModeEnum.FULL_PAYMENT;
            case TH_CG_DJTP:
                return FinancialDocumentsModeEnum.PURCHASE_DEPOSIT_SA;
            default:
                return FinancialDocumentsModeEnum.REFUND;
        }
    }

    public static FinancialDocumentsOriginEnum convertOrigin(int code) {
        switch (fromValue(code)) {
            case TH_CG_DJ:
            case TH_CG_BH:
            case TH_CG_PL:
            case TH_JS:
            case TH_CG_QK:
            case TH_CG_DJTP:
                return FinancialDocumentsOriginEnum.TH_CG;
            case GR_JS:
            case GR_HS_JHS:
            case GR_HS_ZH:
                return FinancialDocumentsOriginEnum.GR_HS;
            case GR_HG_JHS:
            case GR_HG_ZH:
                return FinancialDocumentsOriginEnum.GR_HG;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }

    public static FinancialClassificationEnum convertClassification(BusinessBillTypeEnum code) {
        switch (code) {
            case TH_CG_DJ:
            case TH_CG_BH:
            case TH_CG_PL:
            case TH_CG_QK:
            case TH_CG_DJTP:
                return FinancialClassificationEnum.TH_CG;
            case TH_JS:
                return FinancialClassificationEnum.TH_JS;
            case GR_JS:
                return FinancialClassificationEnum.GR_JS;
            case GR_HS_JHS:
            case GR_HS_ZH:
                return FinancialClassificationEnum.GR_HS;
            case GR_HG_JHS:
            case GR_HG_ZH:
                return FinancialClassificationEnum.GR_HG;
            case TO_B_XS:
            case TO_B_JS:
                return FinancialClassificationEnum.TH_XS;
            case TO_C_XS:
            case TO_C_ON_LINE:
                return FinancialClassificationEnum.GR_XS;
            default:
                throw new BusinessException(ExceptionCode.ENUM_TYPE_NOT_SUPPORT);
        }
    }
}
