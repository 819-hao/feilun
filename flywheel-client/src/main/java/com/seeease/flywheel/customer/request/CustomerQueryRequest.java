package com.seeease.flywheel.customer.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * @author wbh
 * @date 2023/3/1
 */
@Data
public class CustomerQueryRequest extends PageRequest {

    private Integer type;

    private String searchCriteria;

}
