package com.seeease.flywheel.sale;

import com.seeease.flywheel.sale.request.MarketOrderUploadRequest;
import com.seeease.flywheel.sale.request.SaleOrderWarrantyPeriodUpdateRequest;
import com.seeease.flywheel.sale.result.MarketOrderUploadResult;
import com.seeease.flywheel.sale.result.SaleOrderWarrantyPeriodUpdateResult;
import com.seeease.springframework.SingleResponse;

/**
 * 商场订单上传
 *
 * @author Tiro
 * @date 2023/4/13
 */
public interface IMarketOrderUploadFacade {


    /**
     * 订单上传
     *
     * @param request
     * @return
     */
    SingleResponse<MarketOrderUploadResult> upload(MarketOrderUploadRequest request);

    /**
     * 修改质保年限
     * @param request
     * @return
     */
    SaleOrderWarrantyPeriodUpdateResult warrantyPeriodUpdate(SaleOrderWarrantyPeriodUpdateRequest request);
}
