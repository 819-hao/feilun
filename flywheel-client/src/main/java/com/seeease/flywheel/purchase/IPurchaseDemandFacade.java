package com.seeease.flywheel.purchase;



import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.purchase.request.PurchaseDemandConfirmRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandCreateRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandPageRequest;
import com.seeease.flywheel.purchase.request.PurchaseDemandCancelRequest;
import com.seeease.flywheel.purchase.result.PurchaseDemandPageResult;

public interface IPurchaseDemandFacade {

    void create(PurchaseDemandCreateRequest request);

    PageResult<PurchaseDemandPageResult> page(PurchaseDemandPageRequest request);

    void confirm(PurchaseDemandConfirmRequest request);

    void cancelHeadOrder(PurchaseDemandCancelRequest request);


    void mallDepositSalesPayed(String seriesNo);


}
