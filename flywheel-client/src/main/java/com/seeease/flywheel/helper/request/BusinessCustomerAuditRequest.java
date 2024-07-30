package com.seeease.flywheel.helper.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessCustomerAuditRequest implements Serializable {
    /**
     * id
     */
    private Integer id;
    /**
     * 状态
     */
    private Integer status;
}
