package com.seeease.flywheel.web.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/5/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DouYinCustomerDecryptionRequest implements Serializable {
    /**
     * 抖音订单id
     */
    private String douYinOrderId;
    /**
     * 客户id
     */
    private Integer customerId;
    /**
     * 客户联系id
     */
    private Integer customerContactsId;
}
