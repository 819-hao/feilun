package com.seeease.flywheel.serve.storework.event;

import com.seeease.flywheel.serve.base.event.BillHandlerEvent;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 物流发货事件
 *
 * @author Tiro
 * @date 2023/3/9
 */
@Getter
@AllArgsConstructor
public class LogisticsDeliveryEvent implements BillHandlerEvent {

    /**
     * 作业单
     */
    private List<BillStoreWorkPre> workPreList;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 是否门店发货
     */
    private boolean shopDelivery;

    /**
     * 是否必须有QT视频
     */
    private boolean mustQtVideo;
}
