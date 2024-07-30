package com.seeease.flywheel.notify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/7/11
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TextRobotMessage implements RobotMessage {

    @Override
    public MsgType getMsgtype() {
        return MsgType.text;
    }

    private String key;
    /**
     * 消息体
     */
    private Text text;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Text implements Serializable {
        /**
         * 消息内容
         */
        private String content;
        /**
         * 通知的用户
         */
        private List<String> mentioned_list;
    }


}
