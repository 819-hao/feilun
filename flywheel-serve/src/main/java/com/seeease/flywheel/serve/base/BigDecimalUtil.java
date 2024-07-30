package com.seeease.flywheel.serve.base;

import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/20 10:18
 */

public class BigDecimalUtil {

    public static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    /**
     * 数字四舍五入
     *
     * @param bigDecimal
     * @return
     */
    public static BigDecimal roundHalfUp(BigDecimal bigDecimal) {

        if (ObjectUtils.isEmpty(bigDecimal)) {
            return BigDecimal.ZERO;
        }
        //1.四舍五入

        //2.格式化
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 乘法四舍五入
     *
     * @param bigDecimal  乘数
     * @param coefficient 系数
     * @return
     */
    public static BigDecimal multiplyRoundHalfUp(BigDecimal bigDecimal, BigDecimal coefficient) {

        if (ObjectUtils.isEmpty(bigDecimal) || ObjectUtils.isEmpty(coefficient)) {
            return BigDecimal.ZERO;
        }

        return bigDecimal.multiply(coefficient).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divideRoundHalfUp(BigDecimal bigDecimal, BigDecimal coefficient) {

        if (ObjectUtils.isEmpty(bigDecimal) || ObjectUtils.isEmpty(coefficient)) {
            return BigDecimal.ZERO;
        }

        return bigDecimal.divide(coefficient, 2, BigDecimal.ROUND_HALF_UP);
    }

}
