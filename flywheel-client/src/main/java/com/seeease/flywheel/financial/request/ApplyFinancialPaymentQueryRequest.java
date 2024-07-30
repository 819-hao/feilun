package com.seeease.flywheel.financial.request;

import com.seeease.flywheel.PageRequest;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wbh
 * @date 2023/2/27
 */
@Data
public class ApplyFinancialPaymentQueryRequest extends PageRequest {


    /**
     * 搜索条件
     */
    private String searchCriteria;


    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 银行卡id
     */
    private Integer bankId;

    /**
     * 打款金额
     */
    private BigDecimal pricePayment;


    /**
     * 转让协议
     */
    private String agreementTransfer;

    /**
     * 回收定价记录
     */
    private String recoveryPricingRecord;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 父id为主键id
     */
    private Integer parentId;


    //--------------

    /**
     * 创建日期开始
     */
    private String createdTimeStart;

    /**
     * 创建日期结束
     */
    private String createdTimeEnd;

    /**
     * 打款日期开始
     */
    private String paymentTimeStart;

    /**
     * 打款日期结束
     */
    private String paymentTimeEnd;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 打款人
     */
    private String operator;


    /**
     * 门店标签
     */
    private Integer shopId;

    /**
     * 关联单号
     */
    private String originSerialNo;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 是否使用
     */
    private Integer whetherUse;

    /**
     * 使用状态
     */
    private Integer state;

    /**
     * 打款类型（订单类型）
     */
    private Integer typePayment;

    /**
     * 打款类型 todo 新增字段
     */
    private Integer payment;

    /**
     * 业务方式
     * FinancialSalesMethodEnum
     */
    private Integer salesMethod;

    /**
     * 打款主体
     */
    private Integer subjectPayment;

}
