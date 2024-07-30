package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseDemandStatusEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.purchase.enums.PurchaseTypeEnum;
import com.seeease.flywheel.serve.purchase.enums.RecycleModeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购需求列表
 *
 * @TableName bill_purchase
 */
@TableName(value = "bill_purchase_demand", autoResultMap = true)
@Data
public class BillPurchaseDemand extends BaseDomain  {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 型号id
     */
    private Integer goodsWatchId;
    /**
     * 需求成色
     */
    private String fineness;
    /**
     * 需求门店id
     */
    private Integer shopId;
    /**
     * 需求附件
     */
    private String attachment;
    /**
     * 定金金额
     */
    private BigDecimal deposit;
    /**
     * 预计销售价
     */
    private BigDecimal sellPrice;
    /**
     * 客户id
     */
    private Integer contactId;
    /**
     * 关联单号
     */
    private String serial;
    /**
     * 退款金额
     */
    private BigDecimal refundMoney;
    /**
     * 状态
     */
    private PurchaseDemandStatusEnum status;

}