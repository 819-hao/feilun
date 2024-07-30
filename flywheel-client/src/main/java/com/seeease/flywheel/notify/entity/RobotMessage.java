package com.seeease.flywheel.notify.entity;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/7/11
 */
public interface RobotMessage extends Serializable {

    /**
     * 消息类型
     */
    MsgType getMsgtype();

    /**
     * 机器人key
     *
     * @return
     */
    String getKey();

    /**
     * 媒体消息类型
     */
    enum MsgType {
        text,
        markdown
    }
}
