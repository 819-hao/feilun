package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.goods.request.ScrapStockPageRequest;
import com.seeease.flywheel.goods.request.ScrapTransitionAnomalyRequest;
import com.seeease.flywheel.goods.request.ScrappingStockRequest;
import com.seeease.flywheel.goods.result.ScrapStockPageResult;
import com.seeease.flywheel.serve.goods.convert.ScrapStockConverter;
import com.seeease.flywheel.serve.goods.entity.ScrapStock;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.enums.ScrapStockStateEnum;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.mapper.ScrapStockMapper;
import com.seeease.flywheel.serve.goods.mapper.StockMapper;
import com.seeease.flywheel.serve.goods.service.ScrapStockService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author edy
 * @description 针对表【scrap_stock(报废商品)】的数据库操作Service实现
 * @createDate 2023-12-19 14:50:39
 */
@Service
public class ScrapStockServiceImpl extends ServiceImpl<ScrapStockMapper, ScrapStock>
        implements ScrapStockService {

    @Resource
    private StockMapper stockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scrappingStock(ScrappingStockRequest scrappingStockRequest) {
        List<Stock> stocks = stockMapper.selectBatchIds(scrappingStockRequest.getIds());
        Map<Integer, Stock> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getId, Function.identity()));
        stockMap.forEach((stockId, stock) -> {
            Stock stockUpdate = new Stock();
            stockUpdate.setId(stockId);
            stockUpdate.setTransitionStateEnum(StockStatusEnum.TransitionEnum.EXCEPTION_TO_SCRAPPING);
            UpdateByIdCheckState.update(stockMapper, stockUpdate);

            ScrapStock scrapStock = ScrapStockConverter.INSTANCE.convertStock(stock);
            scrapStock.setState(ScrapStockStateEnum.NOT_SCRAPPED);
            scrapStock.setScrapReason(scrappingStockRequest.getScrapReason());
            this.baseMapper.insert(scrapStock);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scrapTransitionAnomaly(ScrapTransitionAnomalyRequest request) {
        List<ScrapStock> scrapStocks = this.baseMapper.selectBatchIds(request.getIds());
        scrapStocks.forEach(s -> {
            Stock stockUpdate = new Stock();
            stockUpdate.setId(s.getStockId());
            stockUpdate.setTransitionStateEnum(StockStatusEnum.TransitionEnum.SCRAPPING_TO_EXCEPTION);
            if (Objects.nonNull(request.getUnusualDesc()))
                stockUpdate.setUnusualDesc(request.getUnusualDesc());
            UpdateByIdCheckState.update(stockMapper, stockUpdate);

        });

        this.baseMapper.deleteBatchIds(request.getIds());
    }

    @Override
    public Page<ScrapStockPageResult> queryPage(ScrapStockPageRequest request) {
        return this.baseMapper.queryPage(Page.of(request.getPage(), request.getLimit()), request);
    }

    @Override
    public void updateStateByStockIds(List<Integer> stockIds, ScrapStockStateEnum stateEnum) {
        this.baseMapper.updateStateByStockIds(stockIds, stateEnum.getValue());
    }

}




