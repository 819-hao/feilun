package com.seeease.flywheel.storework.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Tiro
 * @date 2023/8/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WmsWorkListResult implements Serializable {

    /**
     * 销售位置
     */
    private String saleStoreName;

    /**
     * 发货位置
     */
    private String deliveryStoreName;

    /**
     * 主图
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
     * 公价
     */
    private BigDecimal pricePub;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 附件详情
     */
    private String attachment;

    /**
     * 作业id
     */
    private Integer workId;

    /**
     * 来源
     */
    private Integer workSource;

    /**
     * 预作业单号
     */
    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 备注
     */
    private String saleRemarks;

    /**
     * 数量
     */
    private Integer number;

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
     * 集单状态
     */
    private Integer collectWorkState;

    /**
     * 作业拦截
     */
    private Integer workIntercept;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 物流单状态
     */
    private Integer expressState;

    /**
     * 集单打单状态：1-系统打单，2-人工录入快递单
     */
    private Integer printExpressState;



    /**
     * 销售单-销售时间
     */
    private String saleTime;
    /**
     * 销售单-销售人
     */
    private String saleBy;
    /**
     * 销售单-销售金额
     */
    private String salePrice;
}
