package com.seeease.flywheel.customer.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

@Data
public class CustomerBalancePageRequest extends PageRequest {

    /**
     * 客户名
     */
    private String customerName;

    /**
     * 收款性质
     */
    private Integer type;

    private Integer shopId;

    private List<Integer> customerIdList;
}
