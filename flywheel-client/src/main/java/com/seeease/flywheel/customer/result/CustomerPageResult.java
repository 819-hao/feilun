package com.seeease.flywheel.customer.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPageResult implements Serializable {

    private String phone;

    private String name;

    private Integer customerContactsId;

    private Integer customerId;

    private List<Bank> list;

    @Data
    public static class Bank implements Serializable{
        private Integer id;

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
    }
}
