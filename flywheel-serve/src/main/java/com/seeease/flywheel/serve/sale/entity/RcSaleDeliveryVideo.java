package com.seeease.flywheel.serve.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.sale.entity.SaleDeliveryVideoData;
import com.seeease.flywheel.serve.sale.convert.SaleDeliveryVideoDataMapperTypeHandler;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 销售发货视频记录
 *
 * @TableName rc_sale_delivery_video
 */
@TableName(value = "rc_sale_delivery_video", autoResultMap = true)
@Data
public class RcSaleDeliveryVideo extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 存储方门店id
     */
    private Integer belongingStoreId;

    /**
     * 库存商品id
     */
    private Integer stockId;

    /**
     * 资源数据
     */
    @TableField(typeHandler = SaleDeliveryVideoDataMapperTypeHandler.class)
    private List<SaleDeliveryVideoData> rcData;

    /**
     * 关联销售单id
     */
    private Integer saleId;

    /**
     * 采购备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}