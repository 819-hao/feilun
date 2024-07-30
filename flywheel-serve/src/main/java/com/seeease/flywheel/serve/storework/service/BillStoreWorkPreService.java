package com.seeease.flywheel.serve.storework.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.rfid.result.RfidOutStoreListResult;
import com.seeease.flywheel.rfid.result.RfidShopReceiveListResult;
import com.seeease.flywheel.sale.request.B3SaleReturnOrderListRequest;
import com.seeease.flywheel.sale.result.B3SaleReturnOrderListResult;
import com.seeease.flywheel.serve.storework.entity.BillStoreWorkPre;
import com.seeease.flywheel.serve.storework.enums.StoreWorkReturnTypeEnum;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【bill_store_work_pre(总部入库作业单)】的数据库操作Service
 * @createDate 2023-01-17 11:25:23
 */
public interface BillStoreWorkPreService extends IService<BillStoreWorkPre> {

    /**
     * 创建仓库作业
     *
     * @param request
     * @return
     */
    List<StoreWorkCreateResult> create(List<StoreWorKCreateRequest> request);

    /**
     * 取消仓库作业
     *
     * @param originSerialNo
     */
    void cancel(String originSerialNo);

    /**
     * 物流收货
     *
     * @param request
     * @return
     */
    StoreWorkReceivedListResult receiving(StoreWorkReceivedRequest request);

    /**
     * 仓库入库
     *
     * @param request
     * @return
     */
    StoreWorkInStorageListResult inStorage(StoreWorkInStorageRequest request);

    /**
     * 质检通过
     *
     * @param workId
     */
    void qtPassed(Integer workId, WhetherEnum exceptionMark);

    /**
     * 质检退货待发货
     *
     * @param workId
     */
    void qtRejectWaitForDelivery(Integer workId);

    /**
     * 质检退货返修或者换货
     *
     * @param workId
     * @param returnType 返回类型 1 返修 2 换货
     */
    void qtRejectWaitForDelivery(Integer workId, StoreWorkReturnTypeEnum returnType);

    /**
     * 质检退货待入库
     *
     * @param workId
     */
    void qtRejectWaitForInStorage(Integer workId);

    /**
     * 没有质检直接到待发货
     *
     * @param request
     * @return
     */
    StoreWorkOutStorageResult outStorage(StoreWorkOutStorageRequest request);

    /**
     * 物流发货
     *
     * @param request
     * @return
     */
    StoreWorkDeliveryResult logisticsDelivery(StoreWorkDeliveryRequest request);

    /**
     * 调拨上游发货,通知下游收货
     *
     * @param originSerialNo
     * @param expressNumber
     * @param stockIdList
     */
    void upstreamDelivery(String originSerialNo, String expressNumber, List<Integer> stockIdList);


    /**
     * 调拨上游发货取消
     *
     * @param originSerialNo
     * @param stockIdList
     */
    void upstreamDeliveryOfCancel(String originSerialNo, List<Integer> stockIdList);


    /**
     * 调拨下游退回发货
     *
     * @param originSerialNo
     * @param expressNumber
     * @param stockIdList
     */
    void downstreamDeliveryOfReturned(String originSerialNo, String expressNumber, List<Integer> stockIdList);

    /**
     * 查询
     * @param request
     * @return
     */
    List<StoreWorkDeliveryQueryResult> storeWorkDeliveryQuery(List<StoreWorkDeliveryQueryRequest> request);

    /**
     * rfid查找待出库列表
     * @param shopId 门店id
     * @param q 查询条件
     * @param goodsId 型号
     * @param brandNameList 过滤品牌
     * @return
     */
    List<RfidOutStoreListResult> rfidWaitOutStoreList(Integer shopId, String q, Integer goodsId,List<String> brandNameList);


    /**
     * rfid查找待入库列表
     * @param storeId
     * @param q
     * @param integer
     * @return
     */
    List<RfidShopReceiveListResult> rfidWaitReceiveList(Integer storeId, String q, Integer goodsId);

    /**
     * 3号楼退货查询
     * @param lsShopIds
     * @param b3ShopId
     * @param request
     * @return
     */
    Page<B3SaleReturnOrderListResult> b3Page(List<Integer> b3ShopId, B3SaleReturnOrderListRequest request);


}
