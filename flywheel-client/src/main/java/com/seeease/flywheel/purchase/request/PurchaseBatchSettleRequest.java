package com.seeease.flywheel.purchase.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量结算
 *
 * @author Tiro
 * @date 2023/1/9
 */
@Data
public class PurchaseBatchSettleRequest implements Serializable {

    /**
     * 企业客户名称 客户名自己查询
     */
    private Integer customerId;

    /**
     * 打款主体()??? 限制
     */
    private Integer subjectId;

    /**
     * 备注
     */
    private String remarks;
    /**
     * 开户行 开户地址  ==> {bankAccount}
     */
    private String bank;

    /**
     * 银行名称 中国建行 ==>{bankName}
     */
    private String accountName;

    /**
     * 银行账号 就是卡号 ==>{bankCard}
     */
    private String bankAccount;

    /**
     * 银行客户名称 ==>{bankCustomerName}
     */
    private String bankCustomerName;

    private List<BillPurchaseLineDto> details;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPurchaseLineDto implements Serializable {
        /**
         * 结算价
         */
        private BigDecimal settlePrice;

        /**
         * 关联单号
         */
        private String originSerialNo;

        private String stockSn;

        private Integer stockId;
    }
}
