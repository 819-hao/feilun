package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.goods.request.SeriesPageRequest;
import com.seeease.flywheel.goods.result.SeriesPageResult;
import com.seeease.flywheel.serve.goods.entity.Series;

import java.util.List;

/**
* @author dmmasxnmf
* @description 针对表【series(系列)】的数据库操作Service
* @createDate 2023-01-31 18:50:22
*/
public interface SeriesService extends IService<Series> {

    Page<SeriesPageResult> queryPage(SeriesPageRequest request);

    Integer getSeriesTypeByStockId(Integer id);

    Integer getSeriesTypeByGoodsId(Integer goodsId);

    List<Series> listByName(List<String> nameList);
}
