package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.goods.entity.StockMarkets;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/12/2
 */
public interface StockMarketsService extends IService<StockMarkets> {
    /**
     * @param stockIdList
     * @return
     */
    List<StockMarkets> listByStockId(List<Integer> stockIdList);
}
