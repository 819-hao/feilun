package com.seeease.flywheel.serve.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.dict.entity.StockDict;
import com.seeease.flywheel.serve.dict.mapper.StockDictMapper;
import com.seeease.flywheel.serve.dict.service.StockDictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【stock_dict】的数据库操作Service实现
 * @createDate 2023-02-14 15:19:31
 */
@Service
public class StockDictServiceImpl extends ServiceImpl<StockDictMapper, StockDict>
        implements StockDictService {

    @Override
    public List<StockDict> selectByStockIdList(List<Integer> stockIds) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<StockDict>().in(StockDict::getStockId, stockIds));
    }
}




