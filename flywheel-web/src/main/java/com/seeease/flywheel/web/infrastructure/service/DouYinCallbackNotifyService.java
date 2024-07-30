package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.DouYinCallbackNotify;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;

import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @description 针对表【douyin_callback_notify(抖音消息通知)】的数据库操作Service
 * @createDate 2023-04-25 16:57:01
 */
public interface DouYinCallbackNotifyService extends IService<DouYinCallbackNotify> {

    int insertBatchSomeColumn(List<DouYinCallbackNotify> douYinCallbackNotifyList);

    /**
     * 抖音发货通知
     *
     * @param douYinOrderList
     * @param expressNumber
     */
    Map<Integer, WhetherNotifyEnum> deliveryNotify(List<DouYinOrder> douYinOrderList, String expressNumber);

    void brandNotify(List<Integer> stockIdList);
}
