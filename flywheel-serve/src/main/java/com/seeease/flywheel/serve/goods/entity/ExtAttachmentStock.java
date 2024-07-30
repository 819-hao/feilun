package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 附件库存扩展信息
 *
 * @TableName ext_attachment_stock
 */
@TableName(value = "ext_attachment_stock")
@Data
public class ExtAttachmentStock extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 颜色
     */
    private String colour;

    /**
     * 材质
     */
    private String material;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 适用腕表型号
     */
    private String gwModel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}