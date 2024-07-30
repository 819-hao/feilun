package com.seeease.flywheel.serve.allocate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.allocate.enums.AllocateStateEnum;
import com.seeease.flywheel.serve.allocate.enums.AllocateTypeEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 调拨单
 *
 * @TableName bill_allocate
 */
@TableName(value = "bill_allocate")
@Data
public class BillAllocate extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 调拨单号
     */
    private String serialNo;

    /**
     * 调拨类型:1-寄售,2-寄售归还,3-平调,4-借调
     */
    private AllocateTypeEnum allocateType;

    /**
     * 调拨状态
     */
    private AllocateStateEnum allocateState;

    /**
     * 调拨来源
     */
    private BusinessBillTypeEnum allocateSource;

    /**
     * 调出方
     */
    private Integer fromId;

    /**
     * 调入方
     */
    private Integer toId;

    /**
     * 调出仓库
     */
    private Integer fromStoreId;

    /**
     * 调入仓库
     */
    private Integer toStoreId;

    /**
     * 总成本
     */
    private BigDecimal totalCostPrice;

    /**
     * 总寄售价
     */
    private BigDecimal totalConsignmentPrice;

    /**
     * 数量
     */
    private Integer totalNumber;

    /**
     * 采购备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}