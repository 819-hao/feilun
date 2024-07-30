package com.seeease.flywheel.serve.goods.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;
import com.seeease.flywheel.serve.goods.mapper.BillLifeCycleMapper;
import com.seeease.flywheel.serve.goods.service.BillLifeCycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_life_cycle(生命周期)】的数据库操作Service实现
 * @createDate 2023-03-27 15:22:12
 */
@Service
public class BillLifeCycleServiceImpl extends ServiceImpl<BillLifeCycleMapper, BillLifeCycle>
        implements BillLifeCycleService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBatchSomeColumn(List<BillLifeCycle> billLifeCycleList) {
        return baseMapper.insertBatchSomeColumn(billLifeCycleList);
    }
}




