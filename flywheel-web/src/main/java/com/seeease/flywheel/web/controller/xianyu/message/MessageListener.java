package com.seeease.flywheel.web.controller.xianyu.message;

import com.seeease.flywheel.web.controller.xianyu.enums.XianYuMessageTopicEnum;

/**
 * @author Tiro
 * @date 2023/10/16
 */
public interface MessageListener {
    XianYuMessageTopicEnum getTopic();

    void handle(String content) throws RuntimeException;
}
