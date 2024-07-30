package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

@Data
public class AccountReceiptConfirmPageRequest extends PageRequest {

    /**
     * 创建开始时间
     */
    private String startTime;

    /**
     * 创建结束时间
     */
    private String endTime;

    /**
     * 确认开始时间
     */
    private String confirmStartTime;

    /**
     * 确认结束时间
     */
    private String confirmEndTime;

    /**
     * 申请人
     */
    private String createdBy;

    /**
     * 确认人
     */
    private String updateBy;

    /**
     * 确认收款单号
     */
    private String serialNo;

    /**
     * 状态，0待核销 1部分核销 2已核销
     */
    private Integer status;

    /**
     * 收款类型---CollectionTypeEnum
     */
    private Integer collectionType;

    /**
     * 收款性质
     */
    private Integer collectionNature;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 业务方式
     */
    private Integer salesMethod;

    /**
     * 订单来源---就是门店id
     */
    private Integer storeId;

    /**
     * 订单分类---？
     */
    private Integer originType;

    /**
     * 订单类型---？
     */
    private Integer classification;

    private String payer;

    /**
     * 关联单号
     */
    private String originSerialNo;

    private String stockSn;

}
