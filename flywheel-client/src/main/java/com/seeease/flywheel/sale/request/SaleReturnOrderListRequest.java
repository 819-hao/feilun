package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class SaleReturnOrderListRequest extends PageRequest {

    /**
     * 表身号
     */
    private String stockSn;
    private String wno;

    /**
     * 开始时间
     */
    private String finishStartTime;

    /**
     * 结束时间
     */
    private String finishEndTime;

    /**
     * 开始时间
     */
    private String createdStartTime;

    /**
     * 结束时间
     */
    private String createdEndTime;

    /**
     * 单号
     */
    private String serialNo;
    private String parentSerialNo;

    /**
     * 类型
     */
    private Integer saleReturnType;

    /**
     * 方式
     */
    private Integer saleMode;
    private Integer saleChannel;

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
    private Integer saleReturnState;

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
}
