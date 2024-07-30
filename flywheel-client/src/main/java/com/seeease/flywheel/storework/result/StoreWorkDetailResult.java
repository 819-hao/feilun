package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 详情列表
 *
 * @author dmmasxnmf
 * @Auther Gilbert
 * @Date 2023/1/17 18:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDetailResult implements Serializable {

    /**
     * 作业id
     */
    private Integer id;

    /**
     * 作业单号
     */
    private String serialNo;

    /**
     * 型号图片
     */
    private String image;
    /**
     * 品牌
     */
    private String brandName;

    /**
     * 系列
     */
    private String seriesName;

    /**
     * 型号
     */
    private String model;

    /**
     * 机芯类型
     */
    private String movement;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 成色
     */
    private String finess;

    /**
     * 附件详情
     */
    private String attachment;

    /**
     * 作业由来
     */
    private Integer workSource;

    /**
     * 客户公司名称
     */
    private String customerName;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 快递单号
     */
    private String expressNumber;

    private String deliveryExpressNumber;

    /**
     * 保卡管理-是否已调拨
     */
    private Integer guaranteeCardManage;
}


