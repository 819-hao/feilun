package com.seeease.flywheel.notify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author Tiro
 * @date 2023/8/30
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingReminderNotice extends BaseNotice {
    /**
     * 销售单号
     */
    private String serialNo;

    /**
     * 发货数量
     */
    private Integer count;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;
}