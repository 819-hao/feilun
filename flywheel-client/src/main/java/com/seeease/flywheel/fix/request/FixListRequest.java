package com.seeease.flywheel.fix.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Mr. Du
 * @Description 维修列表
 * @Date create in 2023/1/18 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixListRequest extends PageRequest {

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 维修来源
     */
//    private Integer fixSource;

    /**
     * 流转等级
     */
    private Integer flowGrade;

    /**
     * 是否加急
     */
    private Integer specialExpediting;

    /**
     * 维修状态
     */
    private Integer fixState;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 品牌
     */
    private String brandName;

    /**
     * 型号
     */
//    private String model;

    /**
     * 是否返修
     */
    private Integer repairFlag;

    /**
     * table 是否table
     */
    private Boolean table = null;

    /**
     * 是否超时
     */
    private Boolean timeoutSelect = null;
//
//    /**
//     * 超时文案
//     */
//    private Boolean timeoutMsgSelect = null;

    /**
     * 门店归属
     */
    private Integer storeId;

    /**
     * 维修站点id
     */
    private Integer fixSiteId;

    /**
     * true 送外 false
     */
    private Boolean local;

    /**
     * 订单类型
     */
    private Integer orderType;

    private Boolean finish;

}
