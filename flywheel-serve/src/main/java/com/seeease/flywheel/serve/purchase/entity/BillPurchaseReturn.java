package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @TableName bill_purchase_return
 */
@TableName(value = "bill_purchase_return")
@Data
public class BillPurchaseReturn extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购退货单号
     */
    private String serialNo;

    /**
     * 采购单状态
     */
    @TransitionState
    private BusinessBillStateEnum purchaseReturnState;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 退货金额
     */
    private BigDecimal returnPrice;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否是门店 0 否 1 是
     */

    private WhetherEnum isStore;

    /**
     * 建单门店id
     */
    private Integer storeId;

    /**
     * 发货门店
     */
    private Integer fromStoreId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}