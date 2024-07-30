package com.seeease.flywheel.serve.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.dict.entity.StockDict;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【stock_dict】的数据库操作Service
 * @createDate 2023-02-14 15:19:31
 */
public interface StockDictService extends IService<StockDict> {

    List<StockDict> selectByStockIdList(List<Integer> stockIds);
}
