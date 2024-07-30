package com.seeease.flywheel.goods;

import com.seeease.flywheel.allocate.request.ModelLiveScriptImportRequest;
import com.seeease.flywheel.goods.request.GoodsWatchInfoRequest;
import com.seeease.flywheel.goods.result.GoodsWatchInfo;

import java.util.List;
import java.util.Map;


/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface IGoodsWatchFacade {

    List<GoodsWatchInfo> getAllList(GoodsWatchInfoRequest request);

    String getModelLiveScript(Integer goodsWatchId);

    void excelImport(List<ModelLiveScriptImportRequest.ImportDto> dataList);


    /**
     * 型号对应关系
     *
     * @param request
     * @return
     */
    Map<String, String> listByModeCode(List<String> request);
}
