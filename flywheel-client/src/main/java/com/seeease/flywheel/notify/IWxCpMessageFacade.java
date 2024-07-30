package com.seeease.flywheel.notify;

import com.seeease.flywheel.notify.entity.BaseNotice;
import com.seeease.flywheel.notify.entity.RobotMessage;

/**
 * 企业维修消息客户端
 *
 * @author Tiro
 * @date 2023/5/18
 */
public interface IWxCpMessageFacade {

    /**
     * 发送通知
     *
     * @param baseNotice
     */
    void send(BaseNotice baseNotice);

    /**
     * 发送机器人消息
     *
     * @param robotMessage
     */
    void send(RobotMessage robotMessage);

}