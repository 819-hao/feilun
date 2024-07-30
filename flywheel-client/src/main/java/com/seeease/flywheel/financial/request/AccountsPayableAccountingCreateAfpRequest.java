package com.seeease.flywheel.financial.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wbh
 * @date 2023/5/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountsPayableAccountingCreateAfpRequest implements Serializable {


//    /**
//     * 打款类型
//     */
//    private Integer typePayment;
//
//    /**
//     * 客户id
//     */
//    private Integer customerContactsId;
//
//    /**
//     * 当类型是企业客户时候 从customerName 取
//     * 当类型是个人客户时候 从customerCantactsName取
//     */
//    private String customerName;
//
//    /**
//     * 打款金额
//     */
//    private BigDecimal pricePayment;

    /**
     * 打款主体
     */
    private Integer subjectPayment;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行开户行
     */
    private String bankAccount;

    /**
     * 银行卡号
     */
    private String bankCard;
    /**
     * 收款账户名
     */
    private String bankCustomerName;

//    private Integer whetherUse = 0;

//    private String originSerialNo;

    /**
     * 应收应付id
     */
    private List<AccountsPayableAccountingCreateAfpRequestDto> list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountsPayableAccountingCreateAfpRequestDto implements Serializable {
        /**
         * 结算价
         */
        private BigDecimal settlePrice;

        /**
         * stockId
         */
        private Integer stockId;

        /**
         * 表身号
         */
        private String stockSn;

        /**
         * 关联单号
         */
        private String originSerialNo;
    }
}
