package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.goods.request.ScrapOrderPageRequest;
import com.seeease.flywheel.goods.result.ScrapOrderPageResult;
import com.seeease.flywheel.serve.goods.entity.BillStockScrap;
import com.seeease.flywheel.serve.goods.service.BillStockScrapService;
import com.seeease.flywheel.serve.goods.mapper.BillStockScrapMapper;
import org.springframework.stereotype.Service;

/**
* @author edy
* @description 针对表【bill_stock_scrap(报废单)】的数据库操作Service实现
* @createDate 2023-12-20 16:47:56
*/
@Service
public class BillStockScrapServiceImpl extends ServiceImpl<BillStockScrapMapper, BillStockScrap>
    implements BillStockScrapService{

    @Override
    public Page<ScrapOrderPageResult> queryScrapOrderPage(ScrapOrderPageRequest request) {
        return this.baseMapper.queryScrapOrderPage(Page.of(request.getPage(), request.getLimit()), request);
    }
}




