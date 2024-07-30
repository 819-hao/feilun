package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.request.ScrapOrderPageRequest;
import com.seeease.flywheel.goods.result.ScrapOrderPageResult;
import com.seeease.flywheel.serve.goods.entity.BillStockScrap;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author edy
* @description 针对表【bill_stock_scrap(报废单)】的数据库操作Service
* @createDate 2023-12-20 16:47:56
*/
public interface BillStockScrapService extends IService<BillStockScrap> {

    Page<ScrapOrderPageResult> queryScrapOrderPage(ScrapOrderPageRequest request);
}
