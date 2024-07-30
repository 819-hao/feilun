package com.seeease.flywheel.purchase.request;

import com.seeease.flywheel.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDemandPageRequest extends PageRequest {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 品牌
     */
    private String brandName;
    /**
     * 系列
     */
    private String model;
    /**
     * 客户名称
     */
    private String contactName;
    /**
     * 客户电话
     */
    private String contactPhone;
    /**
     * 状态
     */
    private Integer status;
}
