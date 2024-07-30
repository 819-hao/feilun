package com.seeease.flywheel.goods;

import com.seeease.flywheel.ImportResult;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.goods.entity.StockBaseInfo;
import com.seeease.flywheel.goods.entity.StockInfo;
import com.seeease.flywheel.goods.entity.StockMarketsInfo;
import com.seeease.flywheel.goods.request.*;
import com.seeease.flywheel.goods.result.StockAttachmentResult;
import com.seeease.flywheel.goods.result.StockForNoLoginResult;
import com.seeease.flywheel.goods.result.StockGoodQueryResult;
import com.seeease.flywheel.goods.result.StockPrintResult;
import com.seeease.flywheel.pricing.request.ModelPriceChangeImportRequest;
import com.seeease.flywheel.pricing.result.ModelPriceChangeImportResult;
import com.seeease.flywheel.goods.result.*;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/9
 */
public interface IStockFacade {

    PageResult<StockInfo> selectStockList(StockInfoListRequest request);

    List<StockExt1> selectByStockIdList(List<Integer> stockIdList);
    /**
     * 库存列表
     *
     * @param request
     * @return
     */
    PageResult<StockBaseInfo> listStock(StockListRequest request);


    /**
     * 查询表的信息
     *
     * @param request
     * @return
     */
    List<StockBaseInfo> queryByStockSn(StockQueryRequest request);


    /**
     * @param stockId
     * @return
     */
    StockBaseInfo getById(Integer stockId);

    /**
     * @param wno
     * @return
     */
    StockBaseInfo getByWno(String wno);

    /**
     * @param stockSn
     * @return
     */
    StockBaseInfo getByStockSn(String stockSn);

    /**
     * 附件更新
     *
     * @param request
     */
    StockAttachmentResult attachment(StockAttachmentRequest request);

    /**
     * 打印信息
     *
     * @param request
     * @return
     */
    List<StockPrintResult> print(StockPrintRequest request);

    /**
     * @param wno
     * @return
     */
    StockForNoLoginResult getByWnoNoLogin(String wno);

    void updateStockSn(StockSnUpdateRequest request);

    void updateStockLimitedCode(UpdateStockLimitedCodeRequest request);

    /**
     * 解除定金锁定
     *
     * @param request
     */
    boolean unLockDemand(StockUnLockDemandRequest request);

    /**
     * 型号库存列表
     */
    PageResult<StockGoodQueryResult> modelStockFold(StockGoodQueryRequest request);

    /**
     * 型号变更价格
     *
     * @param request
     * @return
     */
    ImportResult<ModelPriceChangeImportResult> modelPriceChange(ModelPriceChangeImportRequest request);

    /**
     * 同行寄售结算批量导入商品
     * @param request
     * @return
     */
    ImportResult<SettleStockQueryImportResult> settleStockQueryImport(SettleStockQueryImportRequest request);

    ImportResult<StockPromotionImportResult> stockPurchaseUpdate(StockPurchaseUpdateImportRequest request);

    ImportResult<FinancialInvoiceStockQueryImportResult> invoiceStockQueryImport(FinancialInvoiceStockQueryImportRequest request);

    int inExceptionStock(List<Integer> ids);

    /**
     * 不分页查询商品信息
     * @param request
     * @return
     */
    List<StockBaseInfo> listStockByStockSnList(StockListRequest request);
    List<StockBaseInfo> listStockByStockSnList2(StockListRequest request);

    PageResult<StockGoodQueryResult> queryStockPage(StockGoodQueryRequest request);

    /**
     * 保存商品行情
     * @param info
     */
    void saveStockMarketsInfo(StockMarketsInfo  info);

    ImportResult<GroupSettleStockQueryImportResult> settleStockQueryImport(GroupSettleStockQueryImportRequest request);

}
