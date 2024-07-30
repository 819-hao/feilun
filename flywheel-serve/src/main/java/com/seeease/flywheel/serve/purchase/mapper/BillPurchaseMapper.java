package com.seeease.flywheel.serve.purchase.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.financial.request.ApplyFinancialPaymentCheckoutStockSnRequest;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.request.SelectInsertPurchaseRequest;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.purchase.request.PurchaseListRequest;
import com.seeease.flywheel.serve.purchase.entity.BillPurchase;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.math.BigDecimal;

/**
 * @author Tiro
 * @description 针对表【bill_purchase】的数据库操作Mapper
 * @createDate 2023-01-07 17:25:43
 * @Entity com.seeease.flywheel.serve.purchase.entity.BillPurchase
 */
public interface BillPurchaseMapper extends SeeeaseMapper<BillPurchase> {

    Page<BillPurchase> listByRequest(IPage<BillPurchase> page, @Param("request") PurchaseListRequest request);

    /**
     * 查询列表
     *
     * @param page
     * @param request
     * @return
     */
    Page<StockBaseInfo> listByReturn(Page page, @Param("request") StockListRequest request);

    List<String> checkoutStockSn(@Param("request")ApplyFinancialPaymentCheckoutStockSnRequest request);
    BillPurchase selectOneByStockId(@Param("stockId") Integer stockId);

    void updateTotalPrice(@Param("bpId") Integer bpId, @Param("totalPrice") BigDecimal totalPrice);

    /**
     * 查询并插入
     * 单的状态默认值，UNCONFIRMED(1, "待确认"),
     * 传入单总金额
     * 传入单的单号
     * 快递单号置为null
     * 查询那个数据
     *
     * @param request
     * @return
     */
    Integer selectInsert(@Param("request") SelectInsertPurchaseRequest request);

}




