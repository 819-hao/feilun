package com.seeease.flywheel.goods.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/3/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAttachmentRequest extends PageRequest {

    private Integer stockId;


    /**
     * 0 无 1 空白 1 有
     */
    private Integer isCard;

    private String warrantyDate;

    /**
     * 采购附件详情
     */
    private Map<String, List<Integer>> attachmentMap;

    private Integer brandId;

    private Integer seriesId;

    private Integer goodsId;

    private String stockSn;

    /**
     * 表带号
     */
    private String strap;

    private String level;

    private String week;

    /**
     * 异常原因
     */
    private String unusualDesc;

    private String watchSection;
    /**
     * 成色 1。N级/全新、2.S级/99新未使用、3.SA级/98新未使用、4.A级/95新、5.AB级/9新、6.8新及以下
     */
    private String finess;

    private UseScenario useScenario;

    public enum UseScenario {
        /**
         * 附件场景
         */
        ATTACHMENT,
        /**
         * 商品场景
         */
        STOCK,

        /**
         * 异常描述
         */
        UNUSUAL_DESC
    }
}
