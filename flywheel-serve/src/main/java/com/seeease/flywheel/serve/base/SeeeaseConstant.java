package com.seeease.flywheel.serve.base;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 蜥蜴常量类
 *
 * @author wbh
 * @date 2023/2/7
 */
public interface SeeeaseConstant {

    /**
     * 总部id
     */
    int _ZB_ID = 1;

    int _ZB_RIGHT_OF_MANAGEMENT = 20;

    String CG = "CG";
    String CGTH = "CGTH";

    String UN_CONFIRMED = "待确认";
    String UN_STARTED = "待开始";
    String UNDER_WAY = "进行中";
    String COMPLETE = "已完成";
    String CANCEL_WHOLE = "全部取消";
    String IN_RETURN = "已退货";

    String XY_WY_ZB = "稀蜴品牌仓";
    String XY_WY_ZB_FIX = "阿里中心(维修部)";
    String TJ_XXY = "天津稀小蜴";

    /**
     * 参数值
     */
    List<BusinessBillTypeEnum> BUSINESS_BILL_TYPE_ENUM_LIST = Arrays.asList(BusinessBillTypeEnum.TH_CG_BH, BusinessBillTypeEnum.TH_CG_PL, BusinessBillTypeEnum.TH_CG_DJ, BusinessBillTypeEnum.TH_CG_QK, BusinessBillTypeEnum.TH_CG_DJTP, BusinessBillTypeEnum.GR_HG_JHS, BusinessBillTypeEnum.GR_HG_ZH);
    List<BusinessBillTypeEnum> BUSINESS_BILL_TYPE_ENUM_LIST2 = Arrays.asList(BusinessBillTypeEnum.GR_HS_JHS, BusinessBillTypeEnum.GR_HS_ZH);
    List<BusinessBillTypeEnum> BUSINESS_BILL_TYPE_ENUM_LIST3 = Arrays.asList(BusinessBillTypeEnum.TH_JS);

    /**
     * 吊牌价规则
     */
    ImmutableRangeMap<Comparable<BigDecimal>, BigDecimal> TAG_PRICE_ROLE_MAP = ImmutableRangeMap.<Comparable<BigDecimal>, BigDecimal>builder()
            .put(Range.lessThan(BigDecimal.valueOf(10000L)), BigDecimal.valueOf(300L))
            .put(Range.closedOpen(BigDecimal.valueOf(10000L), BigDecimal.valueOf(30000L)), BigDecimal.valueOf(800L))
            .put(Range.closedOpen(BigDecimal.valueOf(30000L), BigDecimal.valueOf(60000L)), BigDecimal.valueOf(1200L))
            .put(Range.closedOpen(BigDecimal.valueOf(60000L), BigDecimal.valueOf(100000L)), BigDecimal.valueOf(2000L))
            .put(Range.atLeast(BigDecimal.valueOf(100000L)), BigDecimal.valueOf(3000L))
            .build();
}
