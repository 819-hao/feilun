package com.seeease.flywheel.storework;

import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;

import java.util.List;

/**
 * 对外提供的入库rpc接口
 *
 * @Auther Gilbert
 * @Date 2023/1/17 17:44
 */
public interface IStoreWorkFacade {

    /**
     * 创建仓库作业
     */
    List<StoreWorkCreateResult> create(List<StoreWorKCreateRequest> request);


    /**
     * 物流收货
     */
    StoreWorkReceivedListResult logisticsReceiving(StoreWorkReceivedRequest request);

    /**
     * 仓库入库
     *
     * @param request
     * @return
     */
    StoreWorkInStorageListResult inStorage(StoreWorkInStorageRequest request);

    /**
     * 出库填充商品
     * @param request
     * @return
     */
    StoreWorkOutStorageSupplyStockResult outStorageSupplyStock(StoreWorkOutStorageSupplyStockRequest request);


    /**
     * 出库
     *
     * @param request
     * @return
     */
    StoreWorkOutStorageResult outStorage(StoreWorkOutStorageRequest request);


    /**
     * 物流发货
     *
     * @param request
     */
    StoreWorkDeliveryResult logisticsDelivery(StoreWorkDeliveryRequest request);

    /**
     * 编辑
     *
     * @param request
     */
    void edit(StoreWorkEditRequest request);

    /**
     * 如果该销售单是商品定金销售
     * @param request
     * @return
     */
    boolean validateCanDoIfMallOrder( List<Integer> workIds);
}
