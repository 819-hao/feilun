package com.seeease.flywheel.serve.storework.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkLogisticsRejectStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 物流收货事件
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Getter
@AllArgsConstructor
public class LogisticsReceivingEvent implements BillHandlerEvent {

    private List<BillStoreWorkPre> workPreList;

    /**
     * 物流收货状态
     */
    private StoreWorkLogisticsRejectStateEnum logisticsRejectState;

    /**
     * 是否门店收货
     */
    private boolean shopReceived;
}
