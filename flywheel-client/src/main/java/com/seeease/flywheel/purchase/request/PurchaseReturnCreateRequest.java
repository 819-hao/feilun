package com.seeease.flywheel.purchase.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/2/1
 */
@Data
public class PurchaseReturnCreateRequest implements Serializable {
    /**
     * 单号
     */
//    private String serialNo;

//    private String purchaseSerialNo;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    private String remarks;

    /**
     * 单据详情
     */
    private List<BillPurchaseReturnLineDto> details;

    @Data
    public static class BillPurchaseReturnLineDto implements Serializable {

        /**
         * 退货价格
         */
        private BigDecimal purchaseReturnPrice;

        private Integer stockId;

        private String remark;

        private String originSerialNo;

        private Integer purchaseType;

        private Integer purchaseSubjectId;

        private Integer locationId;

        private Integer goodsId;
    }

    private Integer storeId;

    private Integer rightOfManagement;
}
