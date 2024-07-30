package com.seeease.flywheel.serve.sale.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.sale.entity.RcSaleDeliveryVideo;
import com.seeease.flywheel.serve.sale.mapper.RcSaleDeliveryVideoMapper;
import com.seeease.flywheel.serve.sale.service.RcSaleDeliveryVideoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【rc_sale_delivery_video(销售发货视频记录)】的数据库操作Service实现
 * @createDate 2023-09-15 10:19:55
 */
@Service
public class RcSaleDeliveryVideoServiceImpl extends ServiceImpl<RcSaleDeliveryVideoMapper, RcSaleDeliveryVideo>
        implements RcSaleDeliveryVideoService {


    @Override
    public int bindSaleOrder(Integer saleId, List<Integer> stockIdList) {
        return baseMapper.bindSaleOrder(saleId, stockIdList);
    }
}




