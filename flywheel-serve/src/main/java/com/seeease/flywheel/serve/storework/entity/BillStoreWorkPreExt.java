package com.seeease.flywheel.serve.storework.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Tiro
 * @date 2023/8/31
 */
@Data
public class BillStoreWorkPreExt extends BillStoreWorkPre {
    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 销售位置
     */
    private Integer saleStoreId;

    /**
     * 发货位置
     */
    private Integer deliveryStoreId;

    /**
     * 销售备注
     */
    private String saleRemarks;

    /**
     * 集单状态
     */
    private Integer collectWorkState;

    /**
     * 集单打单状态：1-系统打单，2-人工录入快递单
     */
    private Integer printExpressState;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;

    /**
     * 抖音抽检码
     */
    private String spotCheckCode;

    /**
     * 销售门店id
     */
    private Integer saleShopId;

    /**
     * 作业拦截
     */
    private Integer workIntercept;

    /**
     * 物流单状态
     */
    private Integer expressState;

    /**
     * 销售单-销售时间
     */
    private Date saleTime;
    /**
     * 销售单-销售人
     */
    private String saleBy;
    /**
     * 销售单-销售金额
     */
    private String salePrice;

    /**
     * 销售渠道
     */
    private Integer saleOrderChannel;
}
