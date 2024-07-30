package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.util.Date;

/**
 * @TableName bill_purchase_plan
 */
@TableName(value = "bill_purchase_plan", autoResultMap = true)
@Data
public class BillPurchasePlan extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购计划单号
     */
    private String serialNo;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

    /**
     * 需方id
     */
    private Integer demanderStoreId;
    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 计划开始时间
     */
    private Date planStartTime;
    /**
     * 计划结束时间
     */
    private Date planEndTime;
    /**
     * 允许修改时间
     */
    private Date enableChangeTime;
    /**
     * 选品时间
     */
    private Date selectionTime;
    /**
     * 需求提交时间
     */
    private Date demandStartTime;
    /**
     * 预估到货时间
     */
    private Date estimatedDeliveryTime;
    /**
     * 业务类型：0-默认其他,1-新表集采
     */
    private Integer businessType;

    private String remarks;

    private static final long serialVersionUID = 1L;
}