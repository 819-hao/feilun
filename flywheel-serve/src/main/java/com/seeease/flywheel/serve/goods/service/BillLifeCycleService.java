package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.goods.entity.BillLifeCycle;

import java.util.List;

/**
 * @author dmmasxnmf
 * @description 针对表【bill_life_cycle(生命周期)】的数据库操作Service
 * @createDate 2023-03-27 15:22:12
 */
public interface BillLifeCycleService extends IService<BillLifeCycle> {

    int insertBatchSomeColumn(List<BillLifeCycle> billLifeCycleList);
}
