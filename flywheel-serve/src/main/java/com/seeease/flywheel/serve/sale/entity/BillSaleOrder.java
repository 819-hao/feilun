package com.seeease.flywheel.serve.sale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.sale.enums.*;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName bill_sale
 */
@TableName(value = "bill_sale_order", autoResultMap = true)
@Data
public class BillSaleOrder extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String serialNo;

    /**
     * 父订单编号
     */
    private String parentSerialNo;

    /**
     * 第三方总订单编号
     */
    private String bizOrderCode;

    /**
     * 类型 1同行 2个人
     */
    private SaleOrderTypeEnum saleType;

    private BusinessBillTypeEnum saleSource;

    /**
     * 销售方式
     */
    private SaleOrderModeEnum saleMode;

    /**
     * 销售渠道
     */
    private SaleOrderChannelEnum saleChannel;

    /**
     * 0 不质检 1-线下质检 2-线上质检
     */
    private SaleOrderInspectionTypeEnum inspectionType;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 第一销售人
     */
    private Integer firstSalesman;

    /**
     * 第二销售人
     */
    private Integer secondSalesman;

    /**
     * 第三销售人
     */
    private Integer thirdSalesman;

    /**
     * 订金金额
     */
    private BigDecimal deposit;

    /**
     * 付款方式
     */
    private SaleOrderPaymentMethodEnum paymentMethod;

    /**
     * 购买原因
     */
    private Integer buyCause;

    /**
     * 总销售价格
     */
    private BigDecimal totalSalePrice;

    /**
     * 销售单状态
     */
    private SaleOrderStateEnum saleState;

    /**
     * 数量
     */
    private Integer saleNumber;

    /**
     * 销售门店id
     */
    private Integer shopId;

    /**
     * 发货位置
     */
    private Integer deliveryLocationId;

    /**
     * 备注
     */
    private String remarks;

    /**
     *
     */
    private Integer customerId;

    private Date finishTime;

    /**
     * 流转人员id
     */
    private Integer transferCustomerId;
    /**
     * '商场的定金采购单是否支付完成 0 未完成 1 完成  该字段仅有销售渠道为商场时起作用
     */
    private Boolean mallPayed;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 销售备注
     */
    private String saleRemarks;

}