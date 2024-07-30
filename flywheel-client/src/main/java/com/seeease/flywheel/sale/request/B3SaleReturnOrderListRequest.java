package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@Data
public class B3SaleReturnOrderListRequest extends PageRequest {
    /**
     * 订单来源
     */
    private Integer origin;
    /**
     * 型号
     */
    private String model;
    /**
     * 表身号
     */
    private String sn;
    /**
     * 订单创建时间
     */
    private String startTime;
    /**
     * 订单创建结束时间
     */
    private String endTime;

    /**
     * 关联单号
     */
    private String atNo;
    /**
     * 发货物流单号
     */
    private String rtNo;
    /**
     * 退货物流单号
     */
    private String stNo;
    /**
     * 抖音销售单号
     */
    private String tiktokNo;
    /**
     * 状态
     * 销售单来源A、B、C、E、F、G、H组且商品位置不在3号楼的 状态为1 ，tab 显示 待质检
     * 销售单来源A、B、C、E、F、G、H组且商品位置在3号楼的 状态为2 ，tab 显示 待收货
     * 销售单来源A、B、C、E、F、G、H组 收货完的状态为3，tab显示已收货
     */
    private Integer status = 1;




}
