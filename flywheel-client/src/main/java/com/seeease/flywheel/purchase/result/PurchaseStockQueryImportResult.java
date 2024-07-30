package com.seeease.flywheel.purchase.result;

import com.seeease.springframework.annotation.ExcelReaderProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr. Du
 * @Description 采购
 * @Date create in 2023/3/31 10:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStockQueryImportResult implements Serializable {

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
     * 成色
     */
    private String finess;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 采购价
     */
    private BigDecimal purchasePrice;


    /**
     * 公价
     */
    private BigDecimal pricePub;


    /**
     * 采购商品型号
     */
    private Integer goodsId;

    /**
     * 销售优先等级
     */
    private Integer salesPriority;

    /**
     * 分级
     */
    private String goodsLevel;

    private String remarks;

    /**
     * 附件集合
     */
    private Map<String, List<Integer>> attachmentMap;

    private Integer isCard;
    private Integer seriesType;

    private String warrantyDate;

    /**
     * 附件文案
     */
    private String attachment;

    private String strapMaterial;


    private BigDecimal wuyuPrice;
}
