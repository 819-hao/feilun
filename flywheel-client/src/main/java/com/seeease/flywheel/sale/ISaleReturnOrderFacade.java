package com.seeease.flywheel.sale;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.*;

import java.util.List;


/**
 * @author wbh
 * @date 2023/3/9
 */
public interface ISaleReturnOrderFacade {

    /**
     * 创建开启销售流程
     * @param request
     * @return
     */
    SaleReturnOrderCreateResult create(SaleReturnOrderCreateRequest request);

    SaleReturnOrderCancelResult cancel(SaleReturnOrderCancelRequest request);

    PageResult<SaleReturnOrderListResult> list(SaleReturnOrderListRequest request);

    SaleReturnOrderDetailsResult details(SaleReturnOrderDetailsRequest request);

    /**
     * 上传快递单号
     *
     * @param request
     * @return
     */
    SaleReturnOrderExpressNumberUploadResult uploadExpressNumber(SaleReturnOrderExpressNumberUploadRequest request);

    ImportResult<SaleReturnStockQueryImportResult> stockQueryImport(SaleReturnStockQueryImportRequest request);

    /**
     * 3号楼退货
     * @param request
     */
    PageResult<B3SaleReturnOrderListResult> b3Page(B3SaleReturnOrderListRequest request);

    /**
     * 3号楼退货添加备注
     * @param request
     */
    void b3AddRemark(B3SaleReturnOrderAddRemarkRequest request);

    /**
     * 跟心快递单号
     * @param trackingNo
     * @param bizCode
     */
    void updateExpressNo(String trackingNo, String bizCode);

    /**
     * 申请退款
     * @param request
     */
    void refund(SaleReturnOrderRefundRequest request);

    /**
     * 错单退货
     * @param request
     */
    void billErrRefund(SaleReturnOrderBillErrRefundRequest request);


    BillSaleReturnOrderResult singleByBizCode(String aftersale_id);

    PageResult<SaleReturnOrderExportResult> exportOrderReturn(SaleReturnOrderExportRequest request);

    List<SaleReturnToTimeoutResult> toTimeout(SaleReturnToTimeoutRequest request);
}
