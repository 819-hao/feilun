package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.goods.enums.ScrapStockStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报废商品
 *
 * @TableName scrap_stock
 */
@TableName(value = "scrap_stock")
@Data
public class ScrapStock extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer stockId;

    /**
     * purchase_subject id 结算主体 买断归属
     */
    private Integer belongId;
    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;

    /**
     * 附件
     */
    private String attachment;
    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Integer locationId;

    /**
     * 经营权（门店ID）
     */
    private Integer rightOfManagement;

    /**
     * 成色 1。N级/全新、2.S级/99新未使用、3.SA级/98新未使用、4.A级/95新、5.AB级/9新、6.8新及以下
     */
    private String finess;

    /**
     * 库存来源
     */
    private Integer stockSrc;

    /**
     * (不要为零ok？)总部采购价格
     */
    private BigDecimal purchasePrice;

    /**
     * 维修成价
     */
    private BigDecimal fixPrice;

    /**
     *
     */
    private BigDecimal addPrice;

    /**
     * tob价
     */
    private BigDecimal tobPrice;

    /**
     * toc价
     */
    private BigDecimal tocPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     *
     */
    private ScrapStockStateEnum state;

    /**
     * 报废原因
     */
    private String scrapReason;
    /**
     * 备注
     */
    private String remark;
    /**
     * cutomer  id
     */
    private Integer ccId;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}