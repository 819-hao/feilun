package com.seeease.flywheel.goods;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.StockAttachmentRequest;
import com.seeease.flywheel.goods.request.StockExceptionListRequest;
import com.seeease.flywheel.goods.request.StockLifeCycleCreateRequest;
import com.seeease.flywheel.goods.request.StockLifecycleListRequest;
import com.seeease.flywheel.goods.result.StockExceptionListResult;
import com.seeease.flywheel.goods.result.StockLifeCycleListResult;

import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 15:27
 */

public interface IStockLifeCycleFacade {

    /**
     * 新建
     *
     * @param request
     */
    void createBatch(List<StockLifeCycleCreateRequest> request);

    /**
     * 分页
     *
     * @param request
     * @return
     */
    PageResult<StockLifeCycleListResult> list(StockLifecycleListRequest request);

    /**
     * 异常商品
     *
     * @param request
     * @return
     */
    PageResult<StockExceptionListResult> exceptionStock(StockExceptionListRequest request);


}
