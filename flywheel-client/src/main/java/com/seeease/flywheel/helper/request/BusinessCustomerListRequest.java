package com.seeease.flywheel.helper.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessCustomerListRequest extends PageRequest {

    /**
     * 联系人
     */
    private String contactName;
    /**
     * 状态
     */
    private Integer status ;
    /**
     * 主键
     */
    private Integer id;
}
