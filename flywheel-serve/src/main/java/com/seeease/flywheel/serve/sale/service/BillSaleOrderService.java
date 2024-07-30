package com.seeease.flywheel.serve.sale.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.sale.request.*;
import com.seeease.flywheel.sale.result.DouYinSaleOrderListResult;
import com.seeease.flywheel.sale.result.SaleOrderListForExportResult;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrder;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderDTO;

import java.util.Date;
import java.util.List;

/**
 * @author edy
 * @description 针对表【bill_sale】的数据库操作Service
 * @createDate 2023-03-06 10:38:19
 */
public interface BillSaleOrderService extends IService<BillSaleOrder> {

    List<BillSaleOrderDTO> create(SaleOrderCreateRequest request);

    void edit(SaleOrderEditRequest request);

    Page<BillSaleOrder> listByRequest(SaleOrderListRequest request);

    BillSaleOrder selectBySerialNo(String originSerialNo);

    BillSaleOrder selectBySaleLineId(Integer saleLineId);

    /**
     * 销售订单确认
     *
     * @param request
     * @return
     */
    BillSaleOrderDTO saleConfirm(SaleOrderConfirmRequest request);

    Page<SaleOrderListForExportResult> export(SaleOrderListRequest request);

    List<BillSaleOrder> queryToCOrderByOffset(Integer currentOffset, Integer limit);

    List<BillSaleOrder> queryToCOrderByFinisTime(Date saleTime);

    List<BillSaleOrder> queryToCOrderByRequest(SaleOrderAccuracyQueryRequest request);

    Integer maxToCOrderByOffset(Integer currentOffset);

    /**
     * 修改抖音订单列表
     *
     * @param douYinOrderIds
     * @param serialNo
     */
    void updateDouYinOrder(List<Integer> douYinOrderIds, Integer i, Date d, String serialNo);

    void updateKuaiShouOrder(List<Integer> kuaiShouOrderIds, Integer i, Date d, String serialNo);

    /**
     * 查询抖音所有已发货 没告知抖音的 单子
     * @param request
     * @return
     */
    List<DouYinSaleOrderListResult> queryDouYinSaleOrder(DouYinSaleOrderListRequest request);

    List<BillSaleOrder> selectBySerialNoList(List<String> xsSale);

    void updateLine(SaleOrderUpdateRequest request);

    Integer selectDouYinOrderBySerialNo(String assocSerialNumber);
}
