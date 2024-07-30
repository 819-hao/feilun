package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.web.entity.XyQtReportVO;
import com.seeease.flywheel.web.entity.XyRecycleOrder;
import com.seeease.flywheel.web.entity.XyRecycleOrderStatsVO;
import com.seeease.flywheel.web.entity.enums.XyRecycleOrderStateEnum;
import com.seeease.flywheel.web.entity.request.*;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【xy_recycle_quote_order(闲鱼估价订单)】的数据库操作Service
 * @createDate 2023-10-20 11:35:18
 */
public interface XyRecycleOrderService extends IService<XyRecycleOrder> {

    /**
     * 根据估价id获取
     *
     * @param quoteId
     * @return
     */
    XyRecycleOrder getByQuoteId(String quoteId);

    /**
     * 根据订单id获取
     *
     * @param bizOrderId
     * @return
     */
    XyRecycleOrder getByBizOrderId(String bizOrderId);

    /**
     * 统计状态数量
     *
     * @return
     */
    List<XyRecycleOrderStatsVO> statsByOrderState();

    /**
     * 更新，带状态机
     *
     * @param quoteOrder
     * @param transitionEnum
     */
    void updateState(XyRecycleOrder quoteOrder, XyRecycleOrderStateEnum.TransitionEnum transitionEnum);

    /**
     * 主动取消回收订单
     *
     * @param request
     */
    void cancelOrder(XyRecycleOrderCancelRequest request);

    /**
     * 第一次报价
     *
     * @param request
     */
    void quote(XyRecycleOrderQuoteRequest request);

    /**
     * 取件
     *
     * @param request
     */
    void pickUp(XyRecycleOrderPickUpRequest request);

    /**
     * 收货
     *
     * @param request
     */
    void received(XyRecycleOrderReceivedRequest request);

    /**
     * 质检
     *
     * @param request
     */
    void qt(XyRecycleOrderQtRequest request);

    /**
     * 打款
     *
     * @param request
     */
    void payment(XyRecycleOrderPaymentRequest request);

    /**
     * 退回
     *
     * @param request
     */
    void refund(XyRecycleOrderRefundRequest request);

    /**
     * 挂载问卷
     *
     * @param spuId
     * @param whole
     */
    void recycleSpu(String spuId, boolean whole);

    /**
     * @param spuId
     * @param whole
     */
    void spuOnline(String spuId, boolean whole);

    /**
     * 质检报告
     *
     * @param qtCode
     * @return
     */
    XyQtReportVO qtReport(String qtCode);
}
