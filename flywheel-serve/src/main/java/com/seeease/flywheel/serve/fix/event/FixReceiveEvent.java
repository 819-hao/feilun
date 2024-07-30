package com.seeease.flywheel.serve.fix.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import lombok.Getter;

/**
 * @Author Mr. Du
 * @Description 通过或者异常
 * @Date create in 2023/3/13 14:21
 */
@Getter
public class FixReceiveEvent implements BillHandlerEvent {

    private Integer fixId;

    public FixReceiveEvent(Integer fixId) {
        this.fixId = fixId;
    }
}
