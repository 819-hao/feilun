package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存管理货架信息
 * @TableName stock_manage_shelves_info
 */
@TableName(value ="stock_manage_shelves_info")
@Data
public class StockManageShelvesInfo extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 型号商品id
     */
    private Integer goodsId;

    /**
     * 速记码
     */
    private String shelvesSimplifiedCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}