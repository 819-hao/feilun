package com.seeease.flywheel.goods.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class GoodsInfo implements Serializable {
    /**
     * 库存id
     */
    private Integer stockId;


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


    /**
     * (不要为零ok？)总部采购价格
     */
    private BigDecimal purchasePrice;

    /**
     * 成色
     */
//    private String finess;


    /**
     * 附件
     */
    private String attachment;

    /**
     * 商品位置
     */
    private String locationName;

    //todo
    /**
     * 经营权（门店ID）
     */
//    private Long rightOfManagement;


    /**
     * 来源主体id
     */
//    private Long sourceSubjectId;

    private String sourceSubjectName;

    private String remark;

}
