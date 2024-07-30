package com.seeease.flywheel.serve.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillStateEnum;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.sale.enums.SaleOrderReturnFlagEnum;
import com.seeease.flywheel.serve.sale.enums.SaleReturnOrderTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName bill_sale_return_order
 */
@TableName(value = "bill_sale_return_order", autoResultMap = true)
@Data
public class BillSaleReturnOrder extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String serialNo;
    private String parentSerialNo;

    /**
     *
     */
    private Integer saleId;

    /**
     * 第三方退货单
     */
    private String bizOrderCode;
    /**
     * 客户联系人
     */
    private Integer customerContactId;
    /**
     *
     */
    private Integer customerId;

    /**
     * 类型 1同行 2个人
     */
    private SaleReturnOrderTypeEnum saleReturnType;

    /**
     *
     */
    private BusinessBillTypeEnum saleReturnSource;

    /**
     * 销售退货单状态
     */
    //@TransitionState
    private BusinessBillStateEnum saleReturnState;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 数量
     */
    private Integer saleReturnNumber;

    /**
     * 总销售成本
     */
    private BigDecimal totalSaleReturnPrice;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 是否错单退货 0否 1是
     */
    private SaleOrderReturnFlagEnum refundFlag;

    private Date finishTime;

    private Integer shopId;

    private Integer deliveryLocationId;

    private String saleSerialNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}