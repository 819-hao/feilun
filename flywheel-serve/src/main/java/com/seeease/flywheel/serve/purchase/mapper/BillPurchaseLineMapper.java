package com.seeease.flywheel.serve.purchase.mapper;

import com.seeease.flywheel.goods.request.SelectInsertPurchaseLineRequest;
import com.seeease.flywheel.purchase.request.PurchaseByNameRequest;
import com.seeease.flywheel.purchase.result.PurchaseByNameResult;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine;
import com.seeease.flywheel.serve.purchase.entity.BillPurchaseLineDetailsVO;
import com.seeease.flywheel.serve.purchase.entity.PurchaseLineDetailsVO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_purchase_line】的数据库操作Mapper
 * @createDate 2023-01-07 17:50:06
 * @Entity com.seeease.flywheel.serve.purchase.entity.BillPurchaseLine
 */
public interface BillPurchaseLineMapper extends SeeeaseMapper<BillPurchaseLine> {


    /**
     * @param purchaseId
     * @return
     */
    List<BillPurchaseLineDetailsVO> selectByPurchaseId(Integer purchaseId);

    /**
     * 查询列表
     *
     * @param request
     * @return
     */
    List<PurchaseByNameResult> getByPurchaseName(@Param("request") PurchaseByNameRequest request);

    /**
     * 根据商品ids 查询同行寄售
     * @param stockIds
     * @param purchaseSource
     * @return
     */
    List<PurchaseLineDetailsVO> listByStockIds(@Param("stockIds") List<Integer> stockIds, @Param("purchaseSource") Integer purchaseSource);

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
    Integer selectInsert(@Param("request") SelectInsertPurchaseLineRequest request);
}




