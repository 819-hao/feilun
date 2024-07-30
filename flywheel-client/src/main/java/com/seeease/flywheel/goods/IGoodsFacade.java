package com.seeease.flywheel.goods;

import com.seeease.flywheel.goods.request.GoodsMetaInfoOffsetBasedRequest;
import com.seeease.flywheel.goods.request.GoodsMetaInfoQueryRequest;
import com.seeease.flywheel.goods.result.GoodsMetaInfoOffsetBasedResult;
import com.seeease.flywheel.goods.result.GoodsMetaInfoQueryResult;

/**
 * @author Tiro
 * @date 2023/1/7
 */
public interface IGoodsFacade {

    /**
     * 基于偏移量的分页查询商品信息
     *
     * @param request
     * @return
     */
    GoodsMetaInfoOffsetBasedResult query(GoodsMetaInfoOffsetBasedRequest request);

    /**
     * 查商品信息
     *
     * @param request
     * @return
     */
    GoodsMetaInfoQueryResult query(GoodsMetaInfoQueryRequest request);

}
