package com.seeease.flywheel.web.event;

import com.seeease.flywheel.fix.request.FixFinishMsgRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @Author Mr. Du
 * @Description 维修完成通知
 * @Date create in 2023/3/21 15:21
 */
@Getter
@Setter
public class FixFinishMsgEvent extends ApplicationEvent {

    private FixFinishMsgRequest fixFinishMsgRequest;

    public FixFinishMsgEvent(Object source, FixFinishMsgRequest fixFinishMsgRequest) {
        super(source);
        this.fixFinishMsgRequest = fixFinishMsgRequest;
    }
}
