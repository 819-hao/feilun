package com.seeease.flywheel.fix.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr. Du
 * @Description 维修送外新建
 * @Date create in 2023/11/15 15:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixForeignMsgRequest implements Serializable {

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 父级维修id
     */
    private Integer parentFixId;

    /**
     * 客户名称(站点名称)
     */
    private String customerName;

    /**
     * 客户手机(站点手机)
     */
    private String customerPhone;
    /**
     * 客户地址(站点地址)
     */
    private String customerAddress;

    /**
     * 新建图片链接列表
     */
    private List<String> newImgList;

    /**
     * 标记维修审核类型
     */
    private Integer tagType;

    /**
     * 收货快递单号
     */
    private String deliveryExpressNo;

    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 表节数
     */
    private String watchSection;

    /**
     * 流转等级
     */
    private Integer flowGrade;

    /**
     * '建单门店id（所属门店）'
     */
    private Integer storeId;

    /**
     * '来源门店id'
     */
    private Integer parentStoreId;

    private String createdBy;

    private Integer createdId;

}
