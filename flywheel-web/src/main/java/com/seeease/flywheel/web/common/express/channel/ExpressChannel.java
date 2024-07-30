package com.seeease.flywheel.web.common.express.channel;

import com.seeease.flywheel.web.entity.ExpressOrder;

/**
 * @author Tiro
 * @date 2023/9/19
 */
public interface ExpressChannel {

    /**
     * 渠道类型
     *
     * @return
     */
    ExpressChannelTypeEnum getChanelType();

    ExpressPlaceOrderResult placeOrder(ExpressPlaceOrder order);

    ExpressRecoveryOrderResult recoveryOrder(ExpressOrder order);
}
