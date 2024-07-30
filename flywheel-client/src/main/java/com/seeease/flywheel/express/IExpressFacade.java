package com.seeease.flywheel.express;

import com.seeease.flywheel.express.request.infrastructure.SfExpressAccessTokenRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressCancelOrderRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressCreateOrderRequest;
import com.seeease.flywheel.express.request.infrastructure.SfExpressTrackOrderRequest;
import com.seeease.flywheel.express.result.infrastructure.SfExpressCancelOrderResult;
import com.seeease.flywheel.express.result.infrastructure.SfExpressCreateOrderResult;
import com.seeease.flywheel.express.result.infrastructure.SfExpressTrackOrderResult;
import com.seeease.flywheel.express.result.infrastructure.SfSfExpressAccessTokenResult;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/6/25 13:46
 */

public interface IExpressFacade {

    /**
     * 获取临时token
     *
     * @param request
     * @return
     */
    SfSfExpressAccessTokenResult getAccessToken(SfExpressAccessTokenRequest request);

    /**
     * 建单接口
     *
     * @param request
     * @return
     */
    SfExpressCreateOrderResult createOrder(SfExpressCreateOrderRequest request);

    /**
     * 查询轨迹
     *
     * @param request
     * @return
     */
    SfExpressTrackOrderResult track(SfExpressTrackOrderRequest request);

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    SfExpressCancelOrderResult cancel(SfExpressCancelOrderRequest request);
}
