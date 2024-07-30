package com.seeease.flywheel.serve.base;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;


/**
 * <p>自增编号工具类</p>
 *
 * @author : yzh
 * @date : 2021-11-16 15:31
 **/
public abstract class SerialIncrUtil {

    private static RedisTemplate<String, Object> serialRedisTemplate;
    private static final String resultTemplate = "%s%s-%s";

    public static void initTemplate() {
        serialRedisTemplate = SpringUtils.getBean("redisTemplate");
    }

    protected static String generateSerial(String serialPrefix) {
        if (serialRedisTemplate == null) initTemplate();
        String nowDate = DateUtils.getNowDate(DateUtils.yyyyMMdd);
        String serialRedisKey = String.format("%s:%s", serialPrefix, nowDate);

        ValueOperations<String, Object> operations = serialRedisTemplate.opsForValue();
        int incrNumber = operations.increment(serialRedisKey, 1).intValue();
        if (incrNumber == 1) {
            operations.set(serialRedisKey, incrNumber, 1, TimeUnit.DAYS);
        }

        //格式转换
        String formatNumber = decimalShiftConvert(incrNumber, 4);
        return String.format(resultTemplate, serialPrefix, nowDate, formatNumber);
    }

    private static String decimalShiftConvert(int placeholderNumber, int fullNumber) {
        String format = String.format("%0" + fullNumber + "d", placeholderNumber);
        int var = Integer.parseInt(format);
        if (String.valueOf(var + 1).length() > format.length()) {
            return String.format("%0" + ++fullNumber + "d", placeholderNumber);
        } else {
            return format;
        }
    }


}
