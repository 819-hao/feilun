package com.seeease.flywheel.notify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/7/11
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MarkdownRobotMessage implements RobotMessage {

    @Override
    public MsgType getMsgtype() {
        return MsgType.markdown;
    }

    private String key;

    /**
     * 消息体
     */
    private Markdown markdown;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Markdown implements Serializable {
        /**
         * 消息内容
         */
        private String content;
    }


}
