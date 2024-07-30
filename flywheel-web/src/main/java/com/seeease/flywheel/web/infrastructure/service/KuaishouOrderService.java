package com.seeease.flywheel.web.infrastructure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.PageResult;
import com.seeease.flywheel.sale.request.KuaiShouOrderConsolidationRequest;
import com.seeease.flywheel.sale.request.KuaiShouOrderListRequest;
import com.seeease.flywheel.sale.result.KuaiShouOrderConsolidationResult;
import com.seeease.flywheel.sale.result.KuaiShouOrderListResult;
import com.seeease.flywheel.web.entity.KuaishouOrder;

/**
 * @author dmmasxnmf
 * @description 针对表【kuaishou_order(快手订单)】的数据库操作Service
 * @createDate 2023-12-01 16:22:28
 */
public interface KuaishouOrderService extends IService<KuaishouOrder> {

    KuaiShouOrderConsolidationResult orderConsolidation(KuaiShouOrderConsolidationRequest request);

    /**
     * 查询pc抖音列表
     *
     * @param request
     * @return
     */
    PageResult<KuaiShouOrderListResult> queryPage(KuaiShouOrderListRequest request);

    void backUseStep(String serialNo);

}
