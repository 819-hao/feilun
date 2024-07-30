package com.seeease.flywheel.serve.storework.entity;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 门店作业事件
 *
 * @author Tiro
 * @date 2023/3/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopWorkEvent implements BillHandlerEvent {
    /**
     * 作业单
     */
    private List<BillStoreWorkPre> source;

    /**
     * 收货单号
     */
    private String expressNumber;

    /**
     * 发货单号
     */
    private String deliveryExpressNumber;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 门店作业事件类型
     */
    public static enum EventType {
        /**
         * 收货事件
         */
        EVENT_RECEIVING,
        /**
         * 拒绝收货事件
         */
        EVENT_REFUSE_RECEIVING,
        /**
         * 发货事件
         */
        EVENT_DELIVERY
    }
}
