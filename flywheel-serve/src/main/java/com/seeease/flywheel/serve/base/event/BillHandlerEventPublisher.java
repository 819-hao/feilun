package com.seeease.flywheel.serve.base.event;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Component
public class BillHandlerEventPublisher {
    @Resource
    private BillHandlerEventContext billHandlerEventContext;

    /**
     * @param event
     */
    public void publishEvent(BillHandlerEvent event) {
        List<BillHandlerEventListener> list = billHandlerEventContext.getHandlerList(event.getClass());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(t -> t.onApplicationEvent(event));
        }
    }
}
