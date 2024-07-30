package com.seeease.flywheel.goods.result;

import com.seeease.flywheel.sale.entity.BuyBackPolicyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/7/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockForNoLoginResult implements Serializable {
    /**
     *
     */
    private Integer stockId;

    private Integer goodsId;
    /**
     *表身号
     */
    private String stockSn;
    /**
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
     * 公价
     */
    private BigDecimal pricePub;
    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 型号主图
     */
    private String image;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 附件信息，
     */
    private String attachment;

    /**
     * 表带材质
     */
    private String strapMaterial;

    private String wno;

    /**
     * 款式
     */
    private String sex;

    /**
     * 表盘形状
     */
    private String shape;
    /**
     * 表带颜色
     */
    private String braceletColor;
    /**
     * 表扣类型
     */
    private String buckleType;
    /**
     * 表壳材质
     */
    private String watchcaseMaterial;
    /**
     * 防水深度
     */
    private String depth;

    List<String> imageList;

    private BigDecimal tocPrice;

    /**
     * 是否有回顾政策 1:是 0:否
     */
    private Integer isRepurchasePolicy;

    private List<BuyBackPolicyInfo> buyBackPolicy;
    /**
     * 型号话术
     */
    private String modelLiveScript;
}
