package com.seeease.flywheel.serve.sale.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seeease.flywheel.serve.sale.entity.RcSaleDeliveryVideo;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【rc_sale_delivery_video(销售发货视频记录)】的数据库操作Service
 * @createDate 2023-09-15 10:19:55
 */
public interface RcSaleDeliveryVideoService extends IService<RcSaleDeliveryVideo> {

    /**
     * 资源绑定销售单
     *
     * @param saleId
     * @param stockIdList
     * @return
     */
    int bindSaleOrder(Integer saleId, List<Integer> stockIdList);
}
