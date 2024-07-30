package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tiro
 * @date 2023/1/19
 */
@Data
public class PurchaseEditRequest implements Serializable {

    /**
     * 采购单id
     */
    private Integer id;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 采购主体id
     */
    private Integer purchaseSubjectId;

    /**
     * 流转主体id
     */
    private Integer viaSubjectId;

    /**
     * 需方id
     */
    private Integer demanderStoreId;

    /**
     * 总采购成本
     */
    private BigDecimal totalPurchasePrice;

    /**
     * 图片
     */
    private List<String> imgList;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 单据详情
     */
    private List<BillPurchaseLineDto> details;


    @Data
    public static class BillPurchaseLineDto implements Serializable {

        /**
         * id
         */
        private Integer id;
        /**
         * 商品型号id
         */
        private Integer goodsId;

        /**
         * 商品编码
         */
        private String wno;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 成色
         */
        private String finess;

        /**
         * 附件列表
         */
        private List<Integer> attachmentList;

        /**
         * 采购价
         */
        private BigDecimal purchasePrice;

        /**
         * 表节
         */
        private String watchSection;

        /**
         * 版本,和表身号构成唯一索引
         */
        private Integer edition;
    }

}
