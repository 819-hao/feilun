package com.seeease.flywheel.web.controller.xianyu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Tiro
 * @date 2023/10/16
 */
@Getter
@AllArgsConstructor
public enum XianYuMessageTopicEnum {
    UNDEFINED("未定义"),
    /**
     * 闲鱼回收业务订单消息
     */
    IDLE_RECYCLE_ORDER_STATE_SYN("idle_recycle_OrderStateSyn"),

    /**
     * 问卷调整消息
     */
    XIANYU_TEMPLATE_QUESCHANGE("xianyu_template_QuesChange"),

    /**
     * 问卷上/下线消息
     */
    XIANYU_TEMPLATE_STATUSCHANGE("xianyu_template_StatusChange"),
    ;

    private String topic;

    public static XianYuMessageTopicEnum fromTopic(String topic) {
        return Arrays.stream(XianYuMessageTopicEnum.values())
                .filter(t -> topic.equals(t.getTopic()))
                .findFirst()
                .orElse(UNDEFINED);
    }
}
