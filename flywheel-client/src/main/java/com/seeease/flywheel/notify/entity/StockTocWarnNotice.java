package com.seeease.flywheel.notify.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @Author Mr. Du
 * @Description 采购需求财务
 * @Date create in 2023/5/18 17:41
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StockTocWarnNotice extends BaseNotice {


    /**
     * 打款单号
     */
    private String stockSn;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 状态描述
     */
    private String state;
}
