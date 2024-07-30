package com.seeease.flywheel.serve.purchase.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCheckoutStockSnRequest;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseLineRequest;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseRequest;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.purchase.request.*;
import com.seeease.flywheel.purchase.result.*;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_purchase】的数据库操作Service
 * @createDate 2023-01-07 17:25:43
 */
public interface BillPurchaseService extends IService<BillPurchase> {

    /**
     * 采购列表
     *
     * @param request
     * @return
     */
    Page<BillPurchase> listByRequest(PurchaseListRequest request);

    /**
     * 采购创建
     *
     * @param dto
     * @return
     */
    PurchaseCreateListResult create(PurchaseCreateRequest dto);

    /**
     * 上传快递单号
     *
     * @param dto
     */
    PurchaseExpressNumberUploadListResult uploadExpressNumber(PurchaseExpressNumberUploadRequest dto);


    /**
     * 采购取消
     *
     * @param request
     * @return
     */
    PurchaseCancelResult cancel(PurchaseCancelRequest request);


    /**
     * 采购编辑
     *
     * @param request
     * @return
     */
    PurchaseEditResult edit(PurchaseEditRequest request);

    /**
     * 更新订单状态
     *
     * @param dto
     * @return
     */
    PurchaseExpressNumberUploadListResult shopReceiving(PurchaseExpressNumberUploadRequest dto);

    /**
     * 归还用户
     *
     * @param dto
     * @return
     */
    PurchaseExpressNumberUploadListResult confirmReturn(PurchaseExpressNumberUploadRequest dto);

    /**
     * 申请结算
     * @param request
     * @return
     */
    PurchaseApplySettlementResult applySettlement(PurchaseApplySettlementRequest request);

    /**
     * 采购退货需要查询
     * @param request
     * @return
     */
    Page<StockBaseInfo> listByReturn(StockListRequest request);

    /**
     * 转回收
     * @param request
     */
    BillPurchase changeRecycle(PurchaseChangeRecycleRequest request);

    /**
     * 延长时间
     * @param request
     */
    void extendTime(PurchaseExtendTimeRequest request);

    /**
     * 返查实际采购人
     * @param request
     * @return
     */
    List<PurchaseByNameResult> getByPurchaseName(PurchaseByNameRequest request);

    List<String> checkoutStockSn(ApplyFinancialPaymentCheckoutStockSnRequest request);

    /**
     * 查询采购单
     * @param originSerialNo
     * @return
     */
    BillPurchase billPurchaseQuery(String originSerialNo);


    BillPurchase selectOneByStockId(Integer stockId);


    /**
     * 表 采购单号
     *
     * @param stockId
     * @param serialNo
     * @param returnNewStockSn
     * @param returnFixRemarks
     * @return
     */
    Boolean editByStock(Integer stockId, String serialNo, String returnNewStockSn, String returnFixRemarks);

    /**
     * 自动创建采购
     *
     * @param selectInsertPurchaseRequest
     * @param selectInsertPurchaseLineRequest
     */
    void autoPurchaseCreate(SelectInsertPurchaseRequest selectInsertPurchaseRequest, SelectInsertPurchaseLineRequest selectInsertPurchaseLineRequest);


    void updateTotalPrice(Integer bpId, BigDecimal totalPrice);
}
