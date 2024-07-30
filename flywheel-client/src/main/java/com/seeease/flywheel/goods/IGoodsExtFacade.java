package com.seeease.flywheel.goods;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.entity.GoodsBaseInfo;
import com.seeease.flywheel.goods.request.GoodsListRequest;
import com.seeease.flywheel.goods.request.GoodsWatchUpdateImportRequest;
import com.seeease.flywheel.goods.result.GoodsWatchUpdateImportResult;

/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface IGoodsExtFacade {

    /**
     * @param request
     * @return
     */
    PageResult<GoodsBaseInfo> listGoods(GoodsListRequest request);

    /**
     * 更新型号数据导入
     *
     * @param request
     * @return
     */
    ImportResult<GoodsWatchUpdateImportResult> updateGoodsWatchImport(GoodsWatchUpdateImportRequest request);

}
