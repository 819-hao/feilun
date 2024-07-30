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
public interface ISaleOrderFacade {

    /**
     * 创建开启销售流程
     *
     * @param request
     * @return
     */
    SaleOrderCreateResult create(SaleOrderCreateRequest request);

    /**
     * 取消销售单
     * @param request
     * @return
     */
    SaleOrderCancelResult cancel(SaleOrderCancelRequest request);

    /**
     * 修改销售单
     * @param request
     */
    void edit(SaleOrderEditRequest request);

    /**
     * 查询销售列表
     * @param request
     * @return
     */
    PageResult<SaleOrderListResult> list(SaleOrderListRequest request);

    /**
     * 销售详情
     * @param request
     * @return
     */
    SaleOrderDetailsResult details(SaleOrderDetailsRequest request);

    /**
     * 查询结算列表
     * @param request
     * @return
     */
    PageResult<SaleOrderSettlementListResult> querySettlementList(SaleOrderSettlementListRequest request);

    /**
     * 批量结算
     * @param request
     * @return
     */
    SaleOrderBatchSettlementResult batchSettlement(SaleOrderBatchSettlementRequest request);

    /**
     * 销售订单确认出库（可更换商品）
     *
     * @param request
     * @return
     */
    SaleOrderConfirmResult saleConfirm(SaleOrderConfirmRequest request);

    /**
     * 天猫结算
     * @param request
     */
    void tmallSettleAccounts(SaleOrderTmallSettleAccountsRequest request);


    /**
     * 同行销售导入查询
     *
     * @param request
     * @return
     */
    ImportResult<SaleStockQueryImportResult> stockQueryImport(SaleStockQueryImportRequest request);

    /**
     * 批量结算商品导入
     * @param request
     * @return
     */
    ImportResult<SaleSellteStockQueryImportResult> stockQueryImport(SaleSettleStockQueryImportRequest request);

    /**
     * 导出
     * @param request
     * @return
     */
    PageResult<SaleOrderListForExportResult> export(SaleOrderListRequest request);

    /**
     * 查询回收
     * @param request
     * @return
     */
    PageResult<SaleOrderRecycleListResult> queryByRecycle(SaleOrderRecycleListRequest request);

    /**
     * 抖音销售单判断
     * @param serialNo
     * @return
     */
    PrintOptionResult printOption(String serialNo);

    /**
     * 销售单判断
     * @param serialNo
     * @return
     */
    PrintOptionResult printOptionByDd(String serialNo);

    List<DouYinSaleOrderListResult> queryDouYinSaleOrder(DouYinSaleOrderListRequest request);

    /**
     * 商城修改质保年限
     * @param request
     */
    void warrantyPeriodUpdate(SaleOrderWarrantyPeriodUpdateRequest request);

    /**
     * 根据销售单号查询
     * @param seriesNo
     * @return
     */
    SingleSaleOrderResult queryBySerialNo(String seriesNo);





    void update(SaleOrderUpdateRequest request);

    List<SaleHistoryResult> saleHistory(String wno);
}
