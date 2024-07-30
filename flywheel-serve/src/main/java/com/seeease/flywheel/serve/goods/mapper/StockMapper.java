package com.seeease.flywheel.serve.goods.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.entity.StockInfo;
import com.seeease.flywheel.goods.request.StockExceptionListRequest;
import com.seeease.flywheel.goods.request.StockGoodQueryRequest;
import com.seeease.flywheel.goods.request.StockInfoListRequest;
import com.seeease.flywheel.goods.request.StockListRequest;
import com.seeease.flywheel.goods.result.StockExceptionListResult;
import com.seeease.flywheel.goods.result.StockGoodQueryResult;
import com.seeease.flywheel.serve.goods.entity.Stock;
import com.seeease.flywheel.serve.goods.entity.StockExt;
import com.seeease.flywheel.serve.goods.entity.StockPo;
import com.seeease.flywheel.serve.goods.entity.StockQuantityDTO;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @description 针对表【stock(库存)】的数据库操作Mapper
 * @createDate 2023-01-10 14:36:05
 * @Entity com.seeease.flywheel.serve.goods.entity.Stock
 */
public interface StockMapper extends SeeeaseMapper<Stock> {

    /**
     * @param stockIdList
     * @return
     */
    List<StockExt> selectByStockIdList(@Param("stockIdList") List<Integer> stockIdList);

    /**
     * 删除异常说明
     *
     * @param stockIdList
     * @return
     */
    int removeUnusualDesc(@Param("stockIdList") List<Integer> stockIdList);


    /**
     * 根据表身号查可动销商品
     *
     * @param stockSnList
     * @param rightOfManagement
     * @return
     */
    List<StockBaseInfo> listSaleableStockBySn(@Param("stockSnList") List<String> stockSnList, @Param("rightOfManagement") Integer rightOfManagement);

    /**
     * @param page
     * @param request
     * @return
     */
    Page<StockBaseInfo> listStock(IPage<StockListRequest> page, @Param("request") StockListRequest request);

    /**
     * 申请
     *
     * @param page
     * @param request
     * @return
     */
    Page<StockBaseInfo> listStockByApply(IPage<StockListRequest> page, @Param("request") StockListRequest request);

    /**
     * 重新计算寄售价
     *
     * @param stockIds
     * @return
     */
    int recalculateConsignmentPrice(@Param("stockIds") List<Integer> stockIds);

    int selectStockToDJCGById(@Param("stockId") Integer stockId);

    List<StockPo> selectStockListByIds(@Param("stockIds") List<Integer> keySet);

    /**
     * 清除需求门店
     *
     * @param ids
     * @return
     */
    int cleanDemandIdByIds(@Param("ids") List<Integer> ids);

    void cleanCkTimeByIds(@Param("ids") List<Integer> ids);

    /**
     * 异常商品列表
     *
     * @param page
     * @param request
     * @return
     */
    Page<StockExceptionListResult> exceptionStock(Page page, @Param("request") StockExceptionListRequest request);

    /**
     * 库龄刷新，在库龄变更的同时刷新销售优先等级，后续不要的话只需要去掉include标签
     *
     * @param stockIdList
     * @return
     */
    int refreshStorageAge(@Param("stockIdList") List<Integer> stockIdList);

    /**
     * 获取寄售价
     *
     * @param stockIds
     * @return
     */
    List<Stock> getConsignmentPrice(@Param("stockIds") List<Integer> stockIds);

    /**
     * 参考寄售价
     *
     * @param stockId
     * @param fixPrice
     * @return
     */
    Stock getConsignmentPrice2(@Param("stockId") Integer stockId, @Param("fixPrice") BigDecimal fixPrice);

    List<Stock> list(@Param("s") Integer storeId, @Param("b") String brand, @Param("m") String model);

    Page<StockInfo> listByRequest(Page<Object> of, @Param("request") StockInfoListRequest request);

    Page<StockGoodQueryResult> modelStockFoldList(Page<Object> of, @Param("request") StockGoodQueryRequest request);

    List<StockGoodQueryResult> modelStockFoldList(@Param("request") StockGoodQueryRequest request);

    Page<StockBaseInfo> listStockBySettlement(Page<Object> of, @Param("request") StockListRequest request);

    Page<StockBaseInfo> listStockByInvoice(Page<Object> of, @Param("request") StockListRequest request);

    /**
     * 统计商品位置可售库存
     *
     * @param goodsIdList
     * @param locationId
     * @return
     */
    List<StockQuantityDTO> countSaleStockQuantity(@Param("goodsIdList") List<Integer> goodsIdList, @Param("locationId") Integer locationId);

    /**
     * 统计商品位置可调拨库存
     *
     * @param goodsIdList
     * @param rightOfManagement
     * @return
     */
    List<StockQuantityDTO> countAllocateStockQuantity(@Param("goodsIdList") List<Integer> goodsIdList, @Param("rightOfManagement") Integer rightOfManagement);


    /**
     * 不分页查询商品信息。根据表身号批量查询
     *
     * @param request
     * @return
     */
    List<StockBaseInfo> listStock(@Param("request") StockListRequest request);

    long queryUnallocatedGoodsCount();

    Integer selectWhetherProtectById(@Param("id") Integer stockId);
}




