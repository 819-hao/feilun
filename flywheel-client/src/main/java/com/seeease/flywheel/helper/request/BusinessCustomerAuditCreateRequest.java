package com.seeease.flywheel.helper.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessCustomerAuditCreateRequest implements Serializable {
    private Integer id;
    /**
     * 公司名称
     */
    private String firmName;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 联系人区域
     */
    private String contactArea;
    /**
     * 联系人地址
     */
    private String contactAddress;
    /**
     * 区域id
     */
    private List<Integer> areaIds;
    /**
     * 客户属性
     */
    private String prop;
    /**
     * 审批人id
     */
    private Integer approver;
}
