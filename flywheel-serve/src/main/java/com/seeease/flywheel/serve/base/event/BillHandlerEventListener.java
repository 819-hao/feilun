package com.seeease.flywheel.serve.base.event;

/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface BillHandlerEventListener<E extends BillHandlerEvent> {
    void onApplicationEvent(E event);
}
