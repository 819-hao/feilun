package com.seeease.flywheel.pricing.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 调价申请列表
 *
 * @author Tiro
 * @date 2024/2/23
 */
@Data
public class ApplyPricingListRequest extends PageRequest implements Serializable {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 申请门店id
     */
    private Integer applyShopId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 审核状态
     */
    private Integer applyStatus;
}
