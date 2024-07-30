package com.seeease.flywheel.serve.sale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.SaleReturnOrderCancelRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderCreateRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderExpressNumberUploadRequest;
import com.seeease.flywheel.sale.request.SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.result.SaleReturnOrderCancelResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderCreateResult;
import com.seeease.flywheel.sale.result.SaleReturnOrderExpressNumberUploadResult;
import com.seeease.flywheel.serve.sale.entity.BillSaleReturnOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.sale.enums.SaleOrderReturnFlagEnum;

/**
* @author edy
* @description 针对表【bill_sale_return_order】的数据库操作Service
* @createDate 2023-03-09 20:01:50
*/
public interface BillSaleReturnOrderService extends IService<BillSaleReturnOrder> {

    SaleReturnOrderCreateResult create(SaleReturnOrderCreateRequest request);

    SaleReturnOrderCancelResult cancel(SaleReturnOrderCancelRequest request);

    Page<BillSaleReturnOrder> listByRequest(SaleReturnOrderListRequest request);

    SaleReturnOrderExpressNumberUploadResult uploadExpressNumber(SaleReturnOrderExpressNumberUploadRequest request);

    BillSaleReturnOrder selectBySriginSerialNo(String originSerialNo);

    void updateExpressNo(String trackingNo, String bizCode);

    void updateRefundFlag(Integer id, SaleOrderReturnFlagEnum refundFlag);

    Integer selectDouYinOrderBySerialNo(String assocSerialNumber);
}
