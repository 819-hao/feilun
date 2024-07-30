package com.seeease.flywheel.goods.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class ScrapStockPageResult implements Serializable {

    /**
     * $column.columnComment
     */
    private Integer id;
    /**
     * 商品图片
     */
    private String image;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String seriesName;
    /**
     * 型号
     */
    private String model;
    /**
     * 表身号
     */
    private String stockSn;
    private Integer stockId;
    /**
     * 供应商
     */
    private String customerName;
    private Integer ccId;
    /**
     * 库存来源
     */
    private Integer stockSrc;
    /**
     * 成色 1。N级/全新、2.S级/99新未使用、3.SA级/98新未使用、4.A级/95新、5.AB级/9新
     */
    private String finess;
    /**
     * 公价
     */
    private BigDecimal pricePub;
    /**
     * 附件信息，
     */
    private String attachment;
    /**
     * 采购价格
     */
    private BigDecimal purchasePrice;
    /**
     * 总价
     */
    private BigDecimal totalPrice;
    /**
     * 备注
     */
    private String remark;
    /**
     * 商品归属
     */
    private String belongName;
    private Integer belongId;
    /**
     * 所处仓库(库存所属)
     */
    private String locationName;
    private Integer locationId;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;
    /**
     * 经营权
     */
    private String rightOfManagementName;

    private Integer rightOfManagement;

    private Integer goodsId;

    private Integer state;

    private String scrapReason;

    private String createdTime;

    private String wno;
}
