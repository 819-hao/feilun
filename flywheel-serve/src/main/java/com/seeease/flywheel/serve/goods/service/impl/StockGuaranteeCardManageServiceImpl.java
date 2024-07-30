package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.StockGuaranteeCardManage;
import com.seeease.flywheel.serve.goods.mapper.StockGuaranteeCardManageMapper;
import com.seeease.flywheel.serve.goods.service.StockGuaranteeCardManageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【stock_guarantee_card_manage(保卡管理)】的数据库操作Service实现
 * @createDate 2023-11-20 10:35:53
 */
@Service
public class StockGuaranteeCardManageServiceImpl extends ServiceImpl<StockGuaranteeCardManageMapper, StockGuaranteeCardManage>
        implements StockGuaranteeCardManageService {


    @Override
    public int allocateOutByStockId(List<Integer> stockIdList, String allocateNo) {
        return baseMapper.allocateOutByStockId(stockIdList, allocateNo);
    }

    @Override
    public int allocateInByStockId(List<Integer> stockIdList) {
        return baseMapper.allocateInByStockId(stockIdList);
    }

    @Override
    public int allocateCancel(String allocateNo) {
        return baseMapper.allocateCancel(allocateNo);
    }

    @Override
    public int insertBatchSomeColumn(List<StockGuaranteeCardManage> manageList) {
        return baseMapper.insertBatchSomeColumn(manageList);
    }

}




