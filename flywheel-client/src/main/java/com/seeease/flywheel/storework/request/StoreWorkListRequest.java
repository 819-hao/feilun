package com.seeease.flywheel.storework.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 * 查询列表页请求
 *
 * @Auther Gilbert
 * @Date 2023/1/18 10:15
 */
@Data
public class StoreWorkListRequest extends PageRequest {

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String  endTime;

    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 品牌
     */
    private String  brandName;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 作业单号
     */
    private String serialNo;

    /**
     * 作业由来
     */
    private Integer workSource;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 质检状态
     */
    private Integer exceptionMark;

    /**
     * 是否需要聚合
     */
    private boolean needAggregation;

    /**
     * 门店综合
     */
    private boolean storeComprehensive;

    /**
     * 型号
     */
    private String model;

    /**
     * 盒子编号
     */
    private String boxNumber;

    /**
     * 需要配件扩张参数
     */
    private boolean needExtParams;
}
