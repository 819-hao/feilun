package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseTask;

/**
* @author dmmasxnmf
* @description 针对表【bill_purchase_task(采购需求任务)】的数据库操作Service
* @createDate 2023-10-25 14:55:21
*/
public interface BillPurchaseTaskService extends IService<BillPurchaseTask> {

    /**
     * 更改状态
     * @param billPurchaseTask
     */
    void updateByState(BillPurchaseTask billPurchaseTask);

}
