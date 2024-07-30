package com.seeease.flywheel.serve.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.financial.enums.FinancialInvoiceStateEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @TableName bill_sale_return_order_line
 */
@TableName(value = "bill_sale_return_order_line", autoResultMap = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BillSaleReturnOrderLine extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer saleReturnId;

    /**
     *
     */
    private Integer saleLineId;

    /**
     * 销售订单单号
     */
    private String saleSerialNo;

    /**
     * 第三方子订单
     */
    private String subOrderCode;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 退货金额
     */
    private BigDecimal returnPrice;


    private Integer goodsId;

    private Integer saleLineState;

    private Integer rightOfManagement;
    private String remark;
    private Integer whetherOperate;
    /**
     * 是否开票 0没开票 1开票中 2已开票
     */
    private FinancialInvoiceStateEnum whetherInvoice;
    /**
     * 最新结算价
     */
    private BigDecimal newSettlePrice;
    /**
     * 单行状态
     */
    @TransitionState
    private SaleReturnOrderLineStateEnum saleReturnLineState;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;

}