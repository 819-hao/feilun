package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@Data
public class PricingLogListRequest extends PageRequest implements Serializable {

    private Integer id;

    private String serialNo;

    private Integer pricingNode;

    private String stockSn;

    private String createdBy;

    private Integer pricingSource;

    private String originSerialNo;

    private String storeWorkSerialNo;
}
