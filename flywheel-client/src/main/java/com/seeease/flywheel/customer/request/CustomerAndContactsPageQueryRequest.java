package com.seeease.flywheel.customer.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * 企业微信-查询客户或者联系人信息
 */
@Data
public class CustomerAndContactsPageQueryRequest extends PageRequest {

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 联系人或者联系方式
     */
    private String searchCriteria;

}
