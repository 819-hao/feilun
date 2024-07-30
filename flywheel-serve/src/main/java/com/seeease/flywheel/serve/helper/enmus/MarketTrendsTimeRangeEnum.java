package com.seeease.flywheel.serve.helper.enmus;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.seeease.flywheel.serve.base.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public enum MarketTrendsTimeRangeEnum implements IEnum<Integer> {
    /**
     * 半年趋势
     */
    HALF_YEARS(1,() -> DateUtils.getStartTime(180)),
    /**
     * 一年趋势
     */
    YEARS(2,()-> DateUtils.getStartTime(360));

    private Integer value;

    private Supplier<LocalDateTime> rangeStart;

    public static MarketTrendsTimeRangeEnum of(Integer value){
        return Arrays.stream(MarketTrendsTimeRangeEnum.values()).filter(v->v.getValue().equals(value)).findFirst().orElseThrow(()-> new IllegalArgumentException("查找不到对应枚举"));
    }



}
