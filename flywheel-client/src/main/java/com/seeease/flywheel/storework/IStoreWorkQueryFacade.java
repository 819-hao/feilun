package com.seeease.flywheel.storework;

import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.rfid.result.RfidWorkDetailResult;
import com.seeease.flywheel.storework.request.*;
import com.seeease.flywheel.storework.result.*;

import java.util.List;

/**
 * @author Tiro
 * @date 2023/3/14
 */
public interface IStoreWorkQueryFacade {

    /**
     * 更据源头单号查询
     * @param originSerialNoList
     * @return
     */
    List<StoreWorkListResult> listByOriginSerialNo(List<String> originSerialNoList);

    /**
     * 收货列表
     *
     * @param request
     * @return
     */
    PageResult<StoreWorkListResult> listReceiving(StoreWorkListRequest request);

    /**
     * 发货列表
     *
     * @param request
     * @return
     */
    PageResult<StoreWorkListResult> listDelivery(StoreWorkListRequest request);

    /**
     * 出库列表
     *
     * @param request
     * @return
     */
    PageResult<StoreWorkListResult> listOutStorage(StoreWorkListRequest request);

    /**
     * 入库列表
     *
     * @param request
     * @return
     */
    PageResult<StoreWorkListResult> listInStorage(StoreWorkListRequest request);

    /**
     * 发货详情
     *
     * @param request
     * @return
     */
    List<StoreWorkDetailResult> deliveryDetail(StoreWorkDeliveryDetailRequest request);

    /**
     * 出库详情
     *
     * @param request
     * @return
     */
    List<StoreWorkDetailResult> outStorageDetails(StoreWorkOutStorageDetailRequest request);


    /**
     * 日志记录
     *
     * @param request
     * @return
     */
    PageResult<StoreWorkLogResult> logList(StoreWorkLogRequest request);


    /**
     * 打印标签数据
     *
     * @param request
     * @return
     */
    List<StoreWorkPrintLabelResult> printLabel(List<Integer> request);

    /**
     * 发货详情
     *
     * @param request
     * @return
     */
    List<StoreWorkDetailResult> deliveryDetailByPrint(StoreWorkDeliveryDetailRequest request);

    /**
     * 导出统计型号表身号数量-待发货
     *
     * @param request
     * @return
     */
    PageResult<StoreWorkListByModeResult> listDeliveryByMode(StoreWorkListByModelRequest request);

    /**
     * rfid 查找出库详情
     *
     * @param request
     * @return
     */
    List<RfidWorkDetailResult> outStorageRfidDetails(StoreWorkOutStorageRfidDetailRequest request);

}
