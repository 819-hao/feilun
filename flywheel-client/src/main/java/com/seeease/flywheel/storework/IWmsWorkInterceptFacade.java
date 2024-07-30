package com.seeease.flywheel.storework;

import com.seeease.flywheel.storework.request.WmsWorkInterceptRequest;
import com.seeease.flywheel.storework.result.WmsWorkInterceptResult;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/8/31
 */
public interface IWmsWorkInterceptFacade {

    /**
     * 发货拦截
     *
     * @param request
     * @return
     */
    WmsWorkInterceptResult intercept(WmsWorkInterceptRequest request);

    /**
     * 拦截
     *
     * @param originSerialNoList
     */
    void checkIntercept(List<String> originSerialNoList);
}
