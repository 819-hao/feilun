package com.seeease.flywheel.customer.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPageQueryResult implements Serializable {

    private Integer customerId;

    private String customerName;

    /**
     * 银行名称
     */
    private String accountName;

    /**
     * 银行开户行
     */
    private String bankAccount;

    /**
     * 银行卡号
     */
    private String bank;
}
