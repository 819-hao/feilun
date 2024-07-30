package com.seeease.flywheel.serve.stocktaking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.stocktaking.convert.StocktakingConverter;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktaking;
import com.seeease.flywheel.serve.stocktaking.entity.BillStocktakingLine;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingLineStateEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingSourceEnum;
import com.seeease.flywheel.serve.stocktaking.enums.StocktakingStateEnum;
import com.seeease.flywheel.serve.stocktaking.mapper.BillStocktakingLineMapper;
import com.seeease.flywheel.serve.stocktaking.mapper.BillStocktakingMapper;
import com.seeease.flywheel.serve.stocktaking.service.BillStocktakingService;
import com.seeease.flywheel.stocktaking.request.StocktakingSubmitRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tiro
 * @description 针对表【bill_stocktaking(盘点单)】的数据库操作Service实现
 * @createDate 2023-06-17 10:26:50
 */
@Service
public class BillStocktakingServiceImpl extends ServiceImpl<BillStocktakingMapper, BillStocktaking>
        implements BillStocktakingService {
    @Resource
    private BillStocktakingLineMapper billStocktakingLineMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(StocktakingSubmitRequest request) {
        BillStocktaking stocktaking = StocktakingConverter.INSTANCE.convert(request);
        stocktaking.setStocktakingState(StocktakingStateEnum.COMPLETE);
        stocktaking.setStocktakingSource(StocktakingSourceEnum.RFID);
        stocktaking.setLossQuantity(request.getLossList().size());
        stocktaking.setProfitQuantity(request.getProfitList().size());
        stocktaking.setMatchQuantity(request.getMatchList().size());


        List<BillStocktakingLine> lineList = new ArrayList<>();

        //盘盈商品
        request.getProfitList().forEach(wno -> {
            BillStocktakingLine line = new BillStocktakingLine();
            line.setWno(wno);
            line.setStocktakingLineState(StocktakingLineStateEnum.PROFIT);
            lineList.add(line);
        });
        //盘亏商品
        request.getLossList().forEach(wno -> {
            BillStocktakingLine line = new BillStocktakingLine();
            line.setWno(wno);
            line.setStocktakingLineState(StocktakingLineStateEnum.LOSS);
            lineList.add(line);
        });
        //无误
        request.getMatchList().forEach(wno -> {
            BillStocktakingLine line = new BillStocktakingLine();
            line.setWno(wno);
            line.setStocktakingLineState(StocktakingLineStateEnum.MATCH);
            lineList.add(line);
        });

        //新增盘点单
        baseMapper.insert(stocktaking);

        //关联盘点单
        lineList.forEach(t -> t.setStocktakingId(stocktaking.getId()));

        //新增盘点明细
        billStocktakingLineMapper.insertBatchSomeColumn(lineList);

        //更新stockId
        billStocktakingLineMapper.updateStockId(stocktaking.getId());
    }
}




