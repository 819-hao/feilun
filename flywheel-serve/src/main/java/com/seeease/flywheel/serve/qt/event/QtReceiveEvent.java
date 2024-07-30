package com.seeease.flywheel.serve.qt.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import lombok.Getter;

/**
 * @Author Mr. Du
 * @Description 通过或者异常
 * @Date create in 2023/3/13 14:21
 */
@Getter
public class QtReceiveEvent implements BillHandlerEvent {

    private Integer stockId;

    private String originSerialNo;

    public QtReceiveEvent(Integer stockId, String originSerialNo) {
        this.stockId = stockId;
        this.originSerialNo = originSerialNo;
    }
}
