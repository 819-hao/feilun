package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description
 * @Date create in 2023/3/21 10:54
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PricingListRequest extends PageRequest implements Serializable {

    private Integer id;

    private String serialNo;

    private Integer pricingState;

    private String stockSn;

    private List<String> stockSnList;

    private String createdBy;

    private Integer pricingSource;

    private String originSerialNo;

    private String storeWorkSerialNo;

    private List<Integer> brandIdList;

    private List<Integer> goodsIdList;

    private String model;
}
