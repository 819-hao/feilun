package com.seeease.flywheel.serve.sale.mapper;

import com.seeease.flywheel.serve.sale.entity.RcSaleDeliveryVideo;
import com.seeease.seeeaseframework.mybatis.SeeeaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Tiro
 * @description 针对表【rc_sale_delivery_video(销售发货视频记录)】的数据库操作Mapper
 * @createDate 2023-09-15 10:19:55
 * @Entity com.seeease.flywheel.serve.sale.entity.RcSaleDeliveryVideo
 */
public interface RcSaleDeliveryVideoMapper extends SeeeaseMapper<RcSaleDeliveryVideo> {

    /**
     * 资源绑定销售单
     *
     * @param saleId
     * @param stockIdList
     * @return
     */
    int bindSaleOrder(@Param("saleId") Integer saleId, @Param("stockIdList") List<Integer> stockIdList);
}




