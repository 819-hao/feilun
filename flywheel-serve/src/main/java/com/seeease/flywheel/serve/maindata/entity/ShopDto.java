package com.seeease.flywheel.serve.maindata.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ShopDto implements Serializable {
    private Integer id;
    private String name;
    private String address;
    private String position;
    private BigDecimal distance;
    private Integer status;
    private String showName;
    private Integer storeId;
}
