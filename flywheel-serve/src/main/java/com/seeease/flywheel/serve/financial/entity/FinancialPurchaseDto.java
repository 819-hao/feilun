package com.seeease.flywheel.serve.financial.entity;

import com.seeease.flywheel.serve.goods.entity.StockPo;
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
public class FinancialPurchaseDto {

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

    private Integer saleMode;
    /**
     * 销售方
     */
    private Integer saleLocationId;

    private Integer divideInto;

    /**
     * 销售详情 map
     */
//    private Map<Integer, BatchSaleSnapshot> bssMap;

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

    /**
     * 是否经过景德镇2号默认0否
     */
    private Integer isJdzTwo;

    private Integer demandId;//需求门店

    private Map<Integer, BigDecimal> serviceFeeMap;
    private Map<Integer, BigDecimal> stockMap;

}
