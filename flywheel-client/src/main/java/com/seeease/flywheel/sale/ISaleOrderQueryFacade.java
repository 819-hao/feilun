package com.seeease.flywheel.sale;

import com.seeease.flywheel.sale.request.SaleOrderAccuracyQueryRequest;
import com.seeease.flywheel.sale.request.SaleOrderOffsetBasedRequest;
import com.seeease.flywheel.sale.request.SaleOrderQueryRequest;
import com.seeease.flywheel.sale.result.SaleOrderOffsetBasedResult;
import com.seeease.flywheel.sale.result.SaleOrderQueryResult;

/**
 * @author Tiro
 * @date 2023/2/17
 */
public interface ISaleOrderQueryFacade {

    /**
     * 基于偏移量的销售单查询
     *
     * @param request
     * @return
     */
    SaleOrderOffsetBasedResult queryToCOrder(SaleOrderOffsetBasedRequest request);

    /**
     * @param request
     * @return
     */
    SaleOrderQueryResult queryToCOrder(SaleOrderQueryRequest request);

    /**
     * 精准查询
     *
     * @param request
     * @return
     */
    SaleOrderQueryResult queryToCOrder(SaleOrderAccuracyQueryRequest request);
}
