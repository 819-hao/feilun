package com.seeease.flywheel.recycle.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 回收、回购请求接口
 *
 * @Auther Gilbert
 * @Date 2023/9/1 10:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RecycleOrderFirstOfferRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 型号
     */
    private Integer goodsId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 报价列表 0 回收价 1 置换价
     */
    private List<BigDecimal> offerList;

    /**
     * 回收价
     */
    private BigDecimal recyclePrice;

    /**
     * 置换价
     */
    private BigDecimal replacementPrice;

    /**
     * 成色
     */
    private String finess;

    /**
     * 附件列表
     */
    private List<Integer> attachmentList;

    private String warrantyDate;

    /**
     * 0 无 1 空白 1 有
     */
    private Integer isCard;

    /**
     * 采购附件详情
     */
    private Map<String, List<Integer>> attachmentMap;

    private String valuationRemark;

    private String valuationImage;

//    private String valuationPriceId;
}
