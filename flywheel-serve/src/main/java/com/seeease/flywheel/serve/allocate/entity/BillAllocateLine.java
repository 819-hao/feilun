package com.seeease.flywheel.serve.allocate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.seeease.flywheel.serve.allocate.enums.AllocateLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

/**
 * 调拨单行
 * @TableName bill_allocate_line
 */
@TableName(value ="bill_allocate_line")
@Data
public class BillAllocateLine extends BaseDomain implements TransitionStateEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 调拨单id
     */
    private Integer allocateId;

    /**
     * 调拨行状态
     */
    @TransitionState
    private AllocateLineStateEnum allocateLineState;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 成本
     */
    private BigDecimal costPrice;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     * 最新结算价
     */
    private BigDecimal newSettlePrice;
    /**
     * 调拨价
     */
    private BigDecimal transferPrice;

    /**
     * 物流单号
     */
    private String expressNumber;

    /**
     * 出库时间
     */
    private Date ckTime;

    /**
     * 原经营权
     */
    private Integer fromRightOfManagement;

    /**
     * 变更后经营权
     */
    private Integer toRightOfManagement;

    /**
     * 保卡管理-是否已调拨
     */
    private Integer guaranteeCardManage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;

}