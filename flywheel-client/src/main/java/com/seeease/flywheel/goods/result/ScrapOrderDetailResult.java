package com.seeease.flywheel.goods.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class ScrapOrderDetailResult implements Serializable {

    /**
     * $column.columnComment
     */
    private Integer id;
    /**
     *
     */
    private String serialNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 报废原因
     */
    private String scrapReason;
    private String createdTime;
    private String createdBy;

    private String batchImagUrl;

    /**
     *
     */
    private List<LineDto> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineDto implements Serializable {

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
         * 商品归属
         */
        private String belongName;
        private Integer belongId;
        /**
         * 所处仓库(库存所属)
         */
        private String locationName;
        private Integer locationId;
    }

}
