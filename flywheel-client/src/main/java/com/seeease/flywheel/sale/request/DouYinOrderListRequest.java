package com.seeease.flywheel.sale.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

/**
 *
 */
@Data
public class DouYinOrderListRequest extends PageRequest {

    /**
     * 型号
     */
    private String goodsModel;

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
    private String usageStartTime;

    /**
     * 结束时间
     */
    private String usageEndTime;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 联系人
     */
    private String decryptPostReceiver;
    /**
     * 联系人电话
     */
    private String decryptPostTel;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 是否使用
     */
    private Integer whetherUse;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;
}
