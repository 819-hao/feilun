package com.seeease.flywheel.purchase.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnDetailsResult implements Serializable {
    /**
     * 采购退货单id
     */
    private Integer id;

    private String serialNo;

    private Integer customerId;

    private String customerName;

    private String accountName;

    private String bank;

    private String bankAccount;

    private Integer customerContactId;

    private String contactName;

    private String contactPhone;

    private String contactAddress;

    /**
     * 总采购成本
     */
    private BigDecimal returnPrice;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 备注
     */
    private String remarks;

    private String createdBy;

    private String createdTime;

    private Integer purchaseReturnState;

    private List<PurchaseReturnLineVO> details;

    @Data
    public static class PurchaseReturnLineVO implements Serializable {

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
         * 公价
         */
        private BigDecimal pricePub;

        /**
         * 机芯类型
         */
        private String movement;

        /**
         * 表径
         */
        private String watchSize;

        private Integer goodsId;

        /**
         * 采购退货价
         */
        private BigDecimal purchaseReturnPrice;

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
        private String attachment;

        /**
         * 采购单行状态
         */
        private Integer purchaseReturnLineState;

        private Integer state;

//        private Integer locationId;

        private String locationName;

//        private Integer purchaseSubjectId;

        private String purchaseSubjectName;

        private String expressNumber;

        private BigDecimal purchasePrice;

        private Integer stockSrc;

        private Integer stockId;
    }
}
