package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.StockMarkets;
import com.seeease.flywheel.serve.goods.mapper.StockMarketsMapper;
import com.seeease.flywheel.serve.goods.service.StockMarketsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/12/2
 */
@Service
public class StockMarketsServiceImpl extends ServiceImpl<StockMarketsMapper, StockMarkets>
        implements StockMarketsService {


    @Override
    public List<StockMarkets> listByStockId(List<Integer> stockIdList) {
        if (CollectionUtils.isEmpty(stockIdList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(Wrappers.<StockMarkets>lambdaQuery()
                .in(StockMarkets::getStockId, stockIdList));
    }

}
