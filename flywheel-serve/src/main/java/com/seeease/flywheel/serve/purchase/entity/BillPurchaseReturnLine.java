package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseReturnLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @TableName bill_purchase_return_line
 */
@TableName(value = "bill_purchase_return_line")
@Data
public class BillPurchaseReturnLine extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购退货单id
     */
    private Integer purchaseReturnId;

    /**
     * 采购单号
     */
    private String originSerialNo;

    /**
     * 采购退货类型
     */
    private BusinessBillTypeEnum purchaseReturnType;

    /**
     * 采购退货价
     */
    private BigDecimal purchaseReturnPrice;

    private BigDecimal purchasePrice;

    /**
     * 采购退货单行状态
     */
//    @TransitionState
    private PurchaseReturnLineStateEnum purchaseReturnLineState;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 模块
     */
    private Integer module;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 备注
     */
    private String remark;


    /**
     * 采购退货主体id
     */
    private Integer purchaseSubjectId;

    private Integer locationId;

    private Integer stockSrc;

    private String expressNumber;

    /**
     * 版本,和表身号构成唯一索引
     */
    private Integer edition;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}