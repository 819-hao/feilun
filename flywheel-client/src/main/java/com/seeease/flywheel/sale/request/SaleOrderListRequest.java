package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class SaleOrderListRequest extends PageRequest {

    /**
     * 表身号
     */
    private String stockSn;
    private String wno;

    /**
     * 开始时间
     */
    private String createdStartTime;

    /**
     * 结束时间
     */
    private String createdEndTime;
    /**
     * 开始时间
     */
    private String finishStartTime;

    /**
     * 结束时间
     */
    private String finishEndTime;

    /**
     * 单号
     */
    private String serialNo;
    private String parentSerialNo;

    /**
     * 类型 1同行 2 个人
     */
    private Integer saleType;

    /**
     * 方式
     */
    private Integer saleMode;

    /**
     *
     */
    private String customerName;
    private String customerPhone;

    /**
     * 创建人
     */
    private String createdBy;


    /**
     * 状态
     */
    private Integer saleState;
    private Integer saleChannel;

    /**
     * 客户id集合
     */
    private List<Integer> customerContactsIdList;
    /**
     * 客户id集合
     */
    private List<Integer> customerIdList;
    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 门店list查询条件
     */
    private List<Integer> shopIds;

    /**
     * 导出手动选择
     */
    private List<Integer> docBatchIds;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;
    /**
     * 分层比例
     */
    private String proportion;
    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private Integer inspectionType;
}
