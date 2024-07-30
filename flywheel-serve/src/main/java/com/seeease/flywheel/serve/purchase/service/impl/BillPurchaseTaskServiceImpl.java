package com.seeease.flywheel.serve.purchase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseTask;
import com.seeease.flywheel.serve.purchase.mapper.BillPurchaseTaskMapper;
import com.seeease.flywheel.serve.purchase.service.BillPurchaseTaskService;
import com.seeease.seeeaseframework.mybatis.transitionstate.UpdateByIdCheckState;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author dmmasxnmf
* @description 针对表【bill_purchase_task(采购需求任务)】的数据库操作Service实现
* @createDate 2023-10-25 14:55:21
*/
@Service
public class BillPurchaseTaskServiceImpl extends ServiceImpl<BillPurchaseTaskMapper, BillPurchaseTask>
    implements BillPurchaseTaskService {

    @Resource
    private BillPurchaseTaskMapper billPurchaseTaskMapper;

    @Override
    public void updateByState(BillPurchaseTask billPurchaseTask) {
        UpdateByIdCheckState.update(billPurchaseTaskMapper, billPurchaseTask);
    }
}




