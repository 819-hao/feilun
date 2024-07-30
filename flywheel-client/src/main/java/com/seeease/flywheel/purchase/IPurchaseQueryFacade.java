package com.seeease.flywheel.purchase;

import com.seeease.flywheel.purchase.request.PurchaseBuyBackRequest;
import com.seeease.flywheel.purchase.result.PurchaseBuyBackResult;
import com.seeease.flywheel.purchase.result.PurchaseCreateListResult;

/**
 * @author Tiro
 * @date 2023/4/18
 */
public interface IPurchaseQueryFacade {

    /**
     * 查询销售是否回购
     *
     * @param request
     * @return
     */
    PurchaseBuyBackResult queryBuyBack(PurchaseBuyBackRequest request);

    /**
     * 自动创建采购单
     *
     * @param workId
     * @return
     */
    PurchaseCreateListResult autoPurchaseCreate(Integer workId);
}
