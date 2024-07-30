package com.seeease.flywheel.serve.base;

/**
 * @author Tiro
 * @date 2023/9/26
 */
public abstract class PJStockSnGenerator extends SerialIncrUtil {

    /**
     * 生成表身号
     *
     * @return
     */
    public static String generateStockSn() {
        return generateSerial("PJ").replaceAll("-", "");
    }

}
