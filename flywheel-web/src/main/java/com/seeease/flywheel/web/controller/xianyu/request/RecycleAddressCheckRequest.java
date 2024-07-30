package com.seeease.flywheel.web.controller.xianyu.request;

import lombok.Data;

/**
 * 闲鱼请求服务商上门地址校验
 *
 * @author Tiro
 * @date 2023/10/17
 */
@Data
public class RecycleAddressCheckRequest extends QiMenBaseRequest {

    /**
     * 省id
     */
    private Long provinceId;
    /**
     * 市id
     */
    private Long cityId;
    /**
     * 区id
     */
    private Long areaId;
    /**
     * 乡镇id
     */
    private Long townId;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 县，区
     */
    private String area;
    /**
     * 乡，镇,街道
     */
    private String town;

    /**
     * 详细地址
     */
    private String fullAddress;

    /**
     * spuId
     */
    private String spuId;

    /**
     * 维度
     */
    private String lat;
    /**
     * 经度
     */
    private String lng;
    /**
     * 回收场景
     */
    private String sceneType;
    /**
     * 子渠道
     */
    private String subChannel;
    /**
     * 运营活动code
     */
    private String yyhdCode;
}
