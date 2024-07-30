package com.seeease.flywheel.web.infrastructure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.seeease.flywheel.web.infrastructure.service.DouYinOrderLineService;
import com.seeease.flywheel.web.infrastructure.mapper.DouYinOrderLineMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Tiro
* @description 针对表【douyin_order_line(抖音订单行)】的数据库操作Service实现
* @createDate 2023-04-27 10:58:27
*/
@Service
public class DouYinOrderLineServiceImpl extends ServiceImpl<DouYinOrderLineMapper, DouYinOrderLine>
    implements DouYinOrderLineService{

    @Override
    public List<DouYinOrderLine> selectListByOrderIds(List<Integer> orderIds) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<DouYinOrderLine>()
                .in(DouYinOrderLine::getOrderId,orderIds));
    }
}




