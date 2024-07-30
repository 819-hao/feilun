package com.seeease.flywheel.serve.recycle.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.recycle.request.MarketRecycleOrderRequest;
import com.seeease.flywheel.recycle.request.RecycleOrderListRequest;
import com.seeease.flywheel.recycle.result.RecycleOrderResult;
import com.seeease.flywheel.recycle.result.RecyclingListResult;
import com.seeease.flywheel.serve.recycle.entity.MallRecyclingOrder;
import lombok.NonNull;

import java.util.List;

/**
 * @Auther Gilbert
 * @Date 2023/9/4 11:04
 */
public interface IRecycleOrderService extends IService<MallRecyclingOrder> {

    /**
     * 创建
     *
     * @param request
     * @return
     */
    RecycleOrderResult create(MarketRecycleOrderRequest request);

    Page<RecyclingListResult> listByRequest(RecycleOrderListRequest request);

    /**
     * 查询回收列表
     *
     * @param request
     * @return
     */
    Page<RecyclingListResult> listByRequestByRecycle(RecycleOrderListRequest request);

    /**
     * 变更service
     *
     * @param request
     */
    void updateRecycleStatus(MallRecyclingOrder request);

    int updateRecycleById(MallRecyclingOrder mallRecyclingOrder);

    MallRecyclingOrder queryBySaleSerialNo(@NonNull String saleSerialNo);

    /**
     * 拦截
     * @param originSerialNoList
     */
    void checkIntercept(List<String> originSerialNoList);

    /**
     * 采购单确认
     * @param originSerialNo
     */
    void checkIntercept(String originSerialNo);


    /**
     * 查询拦截销售
     * @param purchaseId
     */
    List<Integer> intercept(Integer purchaseId);
}
