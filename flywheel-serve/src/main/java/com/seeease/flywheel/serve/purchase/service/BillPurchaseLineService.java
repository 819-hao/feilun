package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.base.PurchaseLineNotice;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLineDetailsVO;
import com.seeease.flywheel.serve.purchase.entity.PurchaseLineDetailsVO;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_purchase_line】的数据库操作Service
 * @createDate 2023-01-07 17:50:06
 */
public interface BillPurchaseLineService extends IService<BillPurchaseLine> {

    /**
     * 查采购行
     *
     * @param purchaseId
     * @return
     */
    List<BillPurchaseLineDetailsVO> selectByPurchaseId(Integer purchaseId);

    /**
     * 通知消息
     *
     * @param lineNotice
     */
    void noticeListener(PurchaseLineNotice lineNotice);


    List<PurchaseLineDetailsVO> listByStockIds(List<Integer> stockIds);


    BillPurchaseLine billPurchaseLineQuery(Integer purchaseId,Integer stockId);
}
