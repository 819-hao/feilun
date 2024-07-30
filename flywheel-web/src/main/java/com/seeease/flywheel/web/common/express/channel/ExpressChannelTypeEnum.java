package com.seeease.flywheel.web.common.express.channel;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tiro
 * @date 2023/9/19
 */
@AllArgsConstructor
@Getter
public enum ExpressChannelTypeEnum implements IEnum<Integer> {

    SF(1, "顺丰"),
    DY_SF(2, "抖音顺丰"),
    KS_SF(3, "快手顺丰"),
    ;
    private Integer value;
    private String desc;
}