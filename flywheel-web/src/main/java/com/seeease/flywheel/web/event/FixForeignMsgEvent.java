package com.seeease.flywheel.web.event;

import com.seeease.flywheel.fix.request.FixCreateRequest;
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
public class FixForeignMsgEvent extends ApplicationEvent {

    private FixCreateRequest fixCreateRequest;

    public FixForeignMsgEvent(Object source, FixCreateRequest fixCreateRequest) {
        super(source);
        this.fixCreateRequest = fixCreateRequest;
    }
}
