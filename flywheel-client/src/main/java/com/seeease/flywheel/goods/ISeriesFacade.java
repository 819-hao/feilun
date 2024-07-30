package com.seeease.flywheel.goods;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.request.SeriesCreateRequest;
import com.seeease.flywheel.goods.request.SeriesDeleteRequest;
import com.seeease.flywheel.goods.request.SeriesPageRequest;
import com.seeease.flywheel.goods.request.SeriesUpdateRequest;
import com.seeease.flywheel.goods.result.SeriesPageResult;

/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface ISeriesFacade {

    PageResult<SeriesPageResult> queryPage(SeriesPageRequest request);

    void batchDelete(SeriesDeleteRequest request);

    void update(SeriesUpdateRequest request);

    void create(SeriesCreateRequest request);
}
