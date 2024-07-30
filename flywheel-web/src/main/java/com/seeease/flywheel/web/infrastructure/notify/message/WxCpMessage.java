package com.seeease.flywheel.web.infrastructure.notify.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/5/18
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class WxCpMessage implements Serializable {
    /**
     * 接收用户
     */
    @JSONField(name = "touser")
    private String toUser;
    /**
     * 消息类型
     */
    @JSONField(name = "msgtype")
    private String msgType;

}
