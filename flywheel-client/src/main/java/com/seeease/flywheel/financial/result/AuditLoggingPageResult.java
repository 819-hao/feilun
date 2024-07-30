package com.seeease.flywheel.financial.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wbh
 * @date 2023/2/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLoggingPageResult implements Serializable {

    private Integer id;

    /**
     * 申请打款id
     */
    private Integer afpId;

    /**
     * 申请打款编号
     */
    private String afpSerialNo;
    /**
     * 确认收款单编号
     */
    private String arcSerialNo;
    private String auditName;

    /**
     * 审核说明
     */
    private String auditDescription;

    /**
     * 类型
     */
    private Integer type;
    /**
     * 订单分类
     */
    private Integer originType;
    /**
     * 分类
     */
    private Integer classification;

    /**
     * 业务方式
     */
    private Integer salesMethod;

    /**
     *
     */
    private Integer status;

    private Integer number;

    /**
     *
     */
    private BigDecimal totalPrice;

    /**
     *
     */
    private Integer customerId;
    private String customerName;
    /**
     * 客户联系人
     */
    private Integer customerContactId;
    private String customerContactName;
    /**
     * 客户类别
     */
    private Integer customerType;

    /**
     * 创建人
     */
    private String applicant;

    private String createdTime;
    
    private Integer shopId;

    private String shopName;
}
