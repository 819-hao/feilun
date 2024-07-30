package com.seeease.flywheel.sale;

import com.seeease.flywheel.sale.request.SaleDeliveryVideoRequest;
import com.seeease.flywheel.sale.result.SaleDeliveryVideoResult;

/**
 * @author Tiro
 * @date 2023/9/14
 */
public interface ISaleDeliveryVideoFacade {

    /**
     * 保存资源
     *
     * @param request
     * @return
     */
    SaleDeliveryVideoResult save(SaleDeliveryVideoRequest request);
}
