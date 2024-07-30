package com.seeease.flywheel.serve.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.entity.StockInfo;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.StockExceptionListResult;
import com.seeease.flywheel.goods.result.StockGoodQueryResult;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.StockQuantityDTO;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【stock(库存)】的数据库操作Service
 * @createDate 2023-01-10 14:36:05
 */
public interface StockService extends IService<Stock> {

    List<Stock> findByStockSn(String sn);

    List<StockExt> selectByStockIdList(List<Integer> stockIdList);

    /**
     * 批量有向更改状态
     *
     * @param stockIds
     * @param transitionEnum
     */
    void updateStockStatus(List<Integer> stockIds, StockStatusEnum.TransitionEnum transitionEnum);

    Page<StockBaseInfo> listStock(StockListRequest request);

    /**
     * 查询申请采购
     *
     * @param request
     * @return
     */
    Page<StockBaseInfo> listStockByApply(StockListRequest request);

    /**
     * 根据表身号查可动销商品
     *
     * @param stockSnList
     * @param rightOfManagement
     * @return
     */
    List<StockBaseInfo> listSaleableStockBySn(List<String> stockSnList, Integer rightOfManagement);

    /**
     * 清除需求门店
     *
     * @param ids
     * @return
     */
    int cleanDemandIdByIds(List<Integer> ids);

    /**
     * 更改破价销售状态
     *
     * @param stockIds
     * @param i
     */
    void updateUnderselling(List<Integer> stockIds, StockUndersellingEnum i);

    void cleanCkTimeById(List<Integer> ids);

    /**
     * 异常商品
     *
     * @param request
     * @return
     */
    PageResult<StockExceptionListResult> exceptionStock(StockExceptionListRequest request);

    /**
     * 附件
     *
     * @param request
     */
    void attachment(StockAttachmentRequest request);

    /**
     * 重新计算寄售价
     *
     * @param ids
     */
    void recalculateConsignmentPrice(List<Integer> ids);

    /**
     * 库龄刷新
     *
     * @param stockIdList
     * @return
     */
    int refreshStorageAge(@Param("stockIdList") List<Integer> stockIdList);

    /**
     * 获取价格
     *
     * @param stockIdList
     * @return
     */
    List<Stock> getConsignmentPrice(@Param("stockIdList") List<Integer> stockIdList);


    List<Stock> list(Integer storeId, @Nullable String brand, @Nullable String model);

    Page<StockInfo> listByRequest(StockInfoListRequest request);

    Page<StockGoodQueryResult> modelStockFoldList(StockGoodQueryRequest request);

    Page<StockBaseInfo> listStockBySettlement(StockListRequest request);

    List<StockQuantityDTO> countAllocateStockQuantity(List<Integer> goodsIdList, Integer rightOfManagement);

    int inExceptionStock(List<Integer> ids);

    /**
     * 查询对应表身号商品
     *
     * @param request
     * @return
     */
    List<StockBaseInfo> listStockByStockSnList(StockListRequest request);

    Page<StockGoodQueryResult> queryStockPage(StockGoodQueryRequest request);


    Page<StockBaseInfo> listStockByInvoice(StockListRequest request);

    long queryUnallocatedGoodsCount();

    Integer selectWhetherProtectById(Integer stockId);

    /**
     * 删除异常说明
     *
     * @param stockIdList
     */
    void removeUnusualDesc(List<Integer> stockIdList);
}
