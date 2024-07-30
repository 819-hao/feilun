package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.purchase.request.PurchaseDemandPageRequest;
import com.seeease.flywheel.purchase.result.PurchaseDemandPageResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseDemand;
import com.seeease.flywheel.serve.purchase.entity.BillPurchasePlanLine;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderDTO;

import java.util.List;


public interface BillPurchaseDemandService extends IService<BillPurchaseDemand> {

    Page<PurchaseDemandPageResult> pageOf(PurchaseDemandPageRequest request);

    void pushMallRealOrder(List<BillSaleOrderDTO> saleOrderDTOList,Integer ccId);
}
