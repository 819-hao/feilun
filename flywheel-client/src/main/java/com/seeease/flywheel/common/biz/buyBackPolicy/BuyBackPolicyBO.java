package com.seeease.flywheel.common.biz.buyBackPolicy;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.seeease.flywheel.FlywheelConstant;
import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import lombok.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Tiro
 * @date 2023/7/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyBackPolicyBO {
    private static final String NEW_FIN = FlywheelConstant.FINESS_S_99_NEW;
    private static final String FIN_98 = "SA级/98新";
    private static final BigDecimal THRESHOLD_1W = new BigDecimal("10000");
    private static final BigDecimal THRESHOLD_2W = new BigDecimal("20000");
    private static final BigDecimal THRESHOLD_4W = new BigDecimal("40000");
    private static final BigDecimal THRESHOLD_8W = new BigDecimal("80000");

    /**
     * 成色
     */
    private String finess;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 款式
     */
    private String sex;

    /**
     * 成交价或预计成交价
     */
    private BigDecimal clinchPrice;

    /**
     * 回购政策
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class BuyBackPolicyResult {
        /**
         * 回购政策
         */
        private List<BuyBackPolicyInfo> infoList;
        /**
         * 描述
         */
        private String desc;
    }


    /**
     * @return
     */
    public BuyBackPolicyResult getBuyBackPolicy() {
        if (NEW_FIN.equals(finess)) {
            return BuyBackPolicyResult.builder()
                    .infoList(Collections.emptyList())
                    .desc("S级/99新商品,不承诺回购")
                    .build();
        } else {
            return this.getBuyBackPolicyByNot99();
        }
    }


    /**
     * 非99新商品回购政策
     *
     * @return
     */
    private BuyBackPolicyResult getBuyBackPolicyByNot99() {
        if (this.brandId == 21 && this.finess.equals(FIN_98)) {
            return BuyBackPolicyResult.builder()
                    .infoList(Collections.emptyList())
                    .desc("欧米茄98新无回购政策,不承诺回购")
                    .build();
        }
        List<BuyBackPolicyRuleEnum> rule1 = Arrays.stream(BuyBackPolicyRuleEnum.values())
                .filter(t -> t.getBrandId() == this.brandId)
                .collect(Collectors.toList());

        if (rule1.isEmpty()) {
            return BuyBackPolicyResult.builder()
                    .infoList(Collections.emptyList())
                    .desc("当前品牌无回购政策,不承诺回购")
                    .build();
        }

        List<BuyBackPolicyRuleEnum> rule2 = rule1.stream().filter(t -> t.getBrandId() == this.brandId
                        && (Objects.isNull(t.getSex()) || t.getSex().equals(this.sex)))
                .collect(Collectors.toList());


        if (rule2.isEmpty()) {
            return BuyBackPolicyResult.builder()
                    .infoList(Collections.emptyList())
                    .desc("该品牌的当前款式(" + this.sex + ")无回购政策,不承诺回购")
                    .build();
        }

        List<BuyBackPolicyRuleEnum> rule3 = rule2.stream()
                .filter(t -> t.getBrandId() == this.brandId
                        && (Objects.isNull(t.getSex()) || t.getSex().equals(this.sex))
                        && t.getRange().contains(clinchPrice))
                .collect(Collectors.toList());

        if (rule3.isEmpty()) {
            return BuyBackPolicyResult.builder()
                    .infoList(Collections.emptyList())
                    .desc("成交价无法满足门槛,不承诺回购")
                    .build();
        }


        return BuyBackPolicyResult.builder()
                .infoList(rule3.stream()
                        .findFirst()
                        .get()
                        .getPolicyEnumList().stream()
                        .map(BuyBackPolicyEnum::toPolicyInfo)
                        .collect(Collectors.toList()))
                .build();
    }


    @AllArgsConstructor
    @Getter
    enum BuyBackPolicyEnum {
        /**
         * 三月政策
         */
        THREE_MONTHS_75(3, new BigDecimal("7.5"), new BigDecimal("0.5")),
        THREE_MONTHS_8(3, new BigDecimal("8"), new BigDecimal("0.5")),
        THREE_MONTHS_85(3, new BigDecimal("8.5"), new BigDecimal("0.5")),
        THREE_MONTHS_9(3, new BigDecimal("9"), new BigDecimal("0.5")),


        /**
         * 一年政策
         */
        ONE_YEAR_65(12, new BigDecimal("6.5"), new BigDecimal("0.5")),
        ONE_YEAR_7(12, new BigDecimal("7"), new BigDecimal("0.5")),
        ONE_YEAR_75(12, new BigDecimal("7.5"), new BigDecimal("0.5")),
        ONE_YEAR_8(12, new BigDecimal("8"), new BigDecimal("0.5")),

        ;

        /**
         * 承诺周期时间
         */
        private int buyBackTime;
        /**
         * 回购折扣
         */
        private BigDecimal discount;
        /**
         * 置换折扣
         */
        private BigDecimal replacementDiscounts;

        /**
         * @return
         */
        public BuyBackPolicyInfo toPolicyInfo() {
            return BuyBackPolicyInfo.builder()
                    .buyBackTime(this.getBuyBackTime())
                    .discount(this.getDiscount())
                    .replacementDiscounts(this.replacementDiscounts)
                    .build();
        }
    }


    @AllArgsConstructor
    @Getter
    enum BuyBackPolicyRuleEnum {
        /**
         * 欧米茄
         */
        OMEGA_1(21, null, Range.openClosed(BigDecimal.ZERO, THRESHOLD_2W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_8, BuyBackPolicyEnum.ONE_YEAR_7)),
        OMEGA_2(21, null, Range.downTo(THRESHOLD_2W, BoundType.OPEN), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_9, BuyBackPolicyEnum.ONE_YEAR_8)),

        /**
         * 卡地亚
         */
        cartier_1(12, "女士", Range.openClosed(BigDecimal.ZERO, THRESHOLD_4W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_8, BuyBackPolicyEnum.ONE_YEAR_7)),
        Cartier_2(12, "女士", Range.downTo(THRESHOLD_4W, BoundType.OPEN), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_9, BuyBackPolicyEnum.ONE_YEAR_8)),

        /**
         * 萧邦
         */
        CHOPARD_1(26, "女士", Range.openClosed(BigDecimal.ZERO, THRESHOLD_4W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_8, BuyBackPolicyEnum.ONE_YEAR_7)),
        CHOPARD_2(26, "女士", Range.downTo(THRESHOLD_4W, BoundType.OPEN), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_9, BuyBackPolicyEnum.ONE_YEAR_8)),

        /**
         * 积家
         */
        JAEGER_1(11, null, Range.openClosed(BigDecimal.ZERO, THRESHOLD_4W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_8, BuyBackPolicyEnum.ONE_YEAR_7)),
        JAEGER_2(11, null, Range.downTo(THRESHOLD_4W, BoundType.OPEN), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_9, BuyBackPolicyEnum.ONE_YEAR_8)),

        /**
         * 万国
         */
        IWC_1(22, "男士", Range.openClosed(BigDecimal.ZERO, THRESHOLD_4W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_8, BuyBackPolicyEnum.ONE_YEAR_7)),
        IWC_2(22, "男士", Range.downTo(THRESHOLD_4W, BoundType.OPEN), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_9, BuyBackPolicyEnum.ONE_YEAR_8)),

        /**
         * 宝珀
         */
        BLANCPAIN_1(2, null, Range.openClosed(BigDecimal.ZERO, THRESHOLD_4W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_8, BuyBackPolicyEnum.ONE_YEAR_7)),
        BLANCPAIN_2(2, null, Range.open(THRESHOLD_4W, THRESHOLD_8W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_85, BuyBackPolicyEnum.ONE_YEAR_75)),
        BLANCPAIN_3(2, null, Range.downTo(THRESHOLD_8W, BoundType.CLOSED), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_9, BuyBackPolicyEnum.ONE_YEAR_8)),

        /**
         * 浪琴
         */
        LONGINES_1(38, null, Range.openClosed(BigDecimal.ZERO, THRESHOLD_1W), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_75, BuyBackPolicyEnum.ONE_YEAR_65)),
        LONGINES_2(38, null, Range.downTo(THRESHOLD_1W, BoundType.OPEN), Arrays.asList(BuyBackPolicyEnum.THREE_MONTHS_85, BuyBackPolicyEnum.ONE_YEAR_75)),

        ;
        private int brandId;
        private String sex;
        private Range range;
        List<BuyBackPolicyEnum> policyEnumList;
    }
}
