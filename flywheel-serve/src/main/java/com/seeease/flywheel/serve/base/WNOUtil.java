package com.seeease.flywheel.serve.base;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Tiro
 * @date 2023/3/15
 */
public abstract class WNOUtil {
    private static int min = 1000000;
    private static int max = 999999999;


    /**
     * 随机生成商品编码
     *
     * @return
     */
    public static String generateWNO() {
        return "XYW" + getRandom().nextInt(min, max);
    }
    public static String generateWNOJ() {
        return "XYJ" + getRandom().nextInt(min, max);
    }
    public static String generateWNOB() {
        return "XYB" + getRandom().nextInt(min, max);
    }
    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }
}
