package com.seeease.flywheel.goods;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.LogStockOptListRequest;
import com.seeease.flywheel.goods.result.LogStockOptListResult;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/27 15:27
 */

public interface IStockLogFacade {

    /**
     * 分页
     *
     * @param request
     * @return
     */
    PageResult<LogStockOptListResult> list(LogStockOptListRequest request);

}
