package com.seeease.flywheel.web.infrastructure.service;

import com.seeease.flywheel.web.entity.DouYinOrderLine;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Tiro
* @description 针对表【douyin_order_line(抖音订单行)】的数据库操作Service
* @createDate 2023-04-27 10:58:27
*/
public interface DouYinOrderLineService extends IService<DouYinOrderLine> {

    List<DouYinOrderLine> selectListByOrderIds(List<Integer> orderIds);
}
