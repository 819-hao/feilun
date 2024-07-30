package com.seeease.flywheel.serve.financial.entity;

import com.seeease.flywheel.serve.goods.entity.StockPo;
import com.seeease.flywheel.serve.sale.entity.BillSaleOrderLine;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author edy
 * @date 2022/9/22
 */
@Data
public class FinancialSalesDto {

    /**
     * $column.columnComment
     */
    private String serialNumber;

    /**
     * $column.columnComment
     */
    private String assocSerialNumber;

    /**
     * 三方订单
     */
    private String thirdNumber;

    /**
     * 供应商id非联系人id！！！
     */
    private Integer customerId;

    /**
     * 订单数量
     */
    private Integer orderNumber;

    /**
     * 订单金额
     */
    private BigDecimal orderMoney;

    /**
     * 销售渠道
     */
    private Integer clcId;

    /**
     * 创建人
     */
    private String createBy;

    private Date createTime;
    /**
     * 销售方
     */
    private Integer saleLocationId;

    private Integer divideInto;

    /**
     * 商品列表
     */
    List<StockPo> stockList;
    /**
     * 订单分类
     */
    private Integer orderType;
    /**
     * 订单类型
     */
    private Integer orderOrigin;

    private Integer saleType;

    private Integer saleMode;

    private Integer belongId;

    private Integer saleId;

    private Map<Integer, BillSaleOrderLine> lineMap;
}
