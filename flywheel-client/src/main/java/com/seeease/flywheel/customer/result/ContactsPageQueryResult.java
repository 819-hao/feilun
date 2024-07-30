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
public class ContactsPageQueryResult implements Serializable {

    private Integer contactId;

    private String contactName;

    /**
     * 银行名称
     */
    private String contactAddress;

    /**
     * 银行开户行
     */
    private String contactPhone;

}
