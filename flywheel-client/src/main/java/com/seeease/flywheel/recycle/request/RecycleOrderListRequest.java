package com.seeease.flywheel.recycle.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;


@Data
public class RecycleOrderListRequest extends PageRequest {

    /**
     * 表身号
     */
    private String stockSn;
    /**
     * 成色
     */
    private String finess;
    /**
     * 型号
     */
    private String model;
    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 系列名称
     */
    private String seriesName;
    /**
     * 品牌id
     */
    private List<Integer> brandId;
    /**
     * 开始时间
     */
    private String beginCreateTime;
    /**
     * 结束时间
     */
    private String endCreateTime;

    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 单状态
     */
    private String state;

    /**
     * 客户经理名称
     */
    private String employeeName;
    /**
     * 回购id
     */
    private List<Integer> recycleId;

    private Integer recycleType;

    private Integer demandId;

    private String customerPhone;

    private String brandName;

    /**
     * 行状态列表
     */
    private List<Integer> lineStateList;
}
