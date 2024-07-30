package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.base.PurchaseReturnLineNotice;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseReturnLine;

import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
public interface BillPurchaseReturnLineService extends IService<BillPurchaseReturnLine> {

    void changeState(Integer primarySourceId, List<Integer> stockIds);

    /**
     * 监听事件
     * @param lineNotice
     */
    void noticeListener(PurchaseReturnLineNotice lineNotice);
}
