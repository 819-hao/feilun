package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.request.DouYinOrderConsolidationRequest;
import com.seeease.flywheel.sale.request.DouYinOrderListRequest;
import com.seeease.flywheel.sale.result.DouYinOrderConsolidationResult;
import com.seeease.flywheel.sale.result.DouYinOrderListResult;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.seeease.flywheel.web.entity.DouYinOrderLine;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【douyin_order(抖音订单)】的数据库操作Service
 * @createDate 2023-04-26 15:08:49
 */
public interface DouYinOrderService extends IService<DouYinOrder> {

    /**
     * 根据抖音订单号获取订单信息
     *
     * @param orderId
     * @return
     */
    DouYinOrder getByDouYinOrderId(String orderId);

    /**
     * 创建订单
     *
     * @param douYinOrder
     * @param lineList
     */
    void create(DouYinOrder douYinOrder, List<DouYinOrderLine> lineList);

    /**
     * 查询pc抖音列表
     * @param request
     * @return
     */
    PageResult<DouYinOrderListResult> queryPage(DouYinOrderListRequest request);

    /**
     * 合并订单
     * @param request
     * @return
     */
    DouYinOrderConsolidationResult orderConsolidation(DouYinOrderConsolidationRequest request);

    List<DouYinOrder> selectListBySerialNo(List<String> serialNoList);

    String selectExpressNumberBySerialNo(String serialNo);

    void backUseStep(String serialNo);
}
