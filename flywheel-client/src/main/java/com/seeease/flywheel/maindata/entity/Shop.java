package com.seeease.flywheel.maindata.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tiro
 * @date 2023/2/17
 */
@Data
public class Shop implements Serializable {

    /**
     * 店铺id
     */
    private Integer shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 商场店铺名称
     */
    private String mallStoreName;
    /**
     * 店铺地址
     */
    private String address;
    /**
     * 经纬度 120.0001,30.0001
     */
    private String position;
    /**
     * 经营状态：
     * 0 系统使用
     * 1 营业中
     * 2 待开业
     */
    private Integer status;

}
