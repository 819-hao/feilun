package com.seeease.flywheel.web.infrastructure.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.KuaishouCallbackNotify;
import com.seeease.flywheel.web.entity.KuaishouOrder;
import com.seeease.flywheel.web.entity.enums.WhetherNotifyEnum;

import java.util.List;
import java.util.Map;

/**
 * @author dmmasxnmf
 * @description 针对表【kuaishou_callback_notify(抖音消息通知)】的数据库操作Service
 * @createDate 2023-11-22 14:46:42
 */
public interface KuaishouCallbackNotifyService extends IService<KuaishouCallbackNotify> {

    Map<Integer, WhetherNotifyEnum> deliveryNotify(List<KuaishouOrder> kuaiShouOrderList, String expressNumber);
}
