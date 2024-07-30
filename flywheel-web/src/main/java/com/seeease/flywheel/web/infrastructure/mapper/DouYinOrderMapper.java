package com.seeease.flywheel.web.infrastructure.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seeease.flywheel.sale.request.DouYinOrderListRequest;
import com.seeease.flywheel.sale.result.DouYinOrderListResult;
import com.seeease.flywheel.web.entity.DouYinOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Tiro
* @description 针对表【douyin_order(抖音订单)】的数据库操作Mapper
* @createDate 2023-04-26 15:08:49
* @Entity com.seeease.flywheel.web.entity.DouYinOrder
*/
public interface DouYinOrderMapper extends BaseMapper<DouYinOrder> {

    Page<DouYinOrderListResult> listByRequest(Page<Object> of,@Param("request") DouYinOrderListRequest request);

    String selectSeriesNameByModel(@Param("model") String model);

    String selectBrandNameByModel(@Param("model") String model);

    String selectSeriesNameByModelCode(@Param("modelCode") String modelCode);

    String selectBrandNameByModelCode(@Param("modelCode") String modelCode);

    String selectExpressNumberBySerialNo(@Param("serialNo") String serialNo);
}




