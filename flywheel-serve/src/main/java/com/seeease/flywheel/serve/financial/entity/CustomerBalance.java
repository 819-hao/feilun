package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 客户余额
 */
@Data
@TableName(value = "customer_balance", autoResultMap = true)
public class CustomerBalance extends BaseDomain {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;

    /**
     * 正常余额，账户余额
     */
    private BigDecimal accountBalance;

    /**
     * 收款性质
     */
    private Integer type;

    /**
     * 经办人，飞轮内部员工id
     */
    private Integer userId;

    /**
     * 门店id
     */
    private Integer shopId;

    /**
     * 关联单号
     */
    private String originSerialNo;
}
