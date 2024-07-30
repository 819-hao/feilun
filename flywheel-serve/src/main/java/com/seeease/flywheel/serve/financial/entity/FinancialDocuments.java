package com.seeease.flywheel.serve.financial.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 财务单据
 *
 * @TableName financial_documents
 */
@TableName(value = "financial_documents", autoResultMap = true)
@Data
public class FinancialDocuments implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String serialNumber;

    /**
     *
     */
    private String assocSerialNumber;

    /**
     * 三方订单
     */
    private String thirdNumber;

    /**
     * 订单类型CGRK(0),CGTH(1),XSCK(2),XSTH(3)
     */
    private Integer orderType;

    /**
     * 订单来源
     */
    private Integer orderOrigin;
    /**
     * 业务方式
     */
    private Integer saleMode;
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
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 出库时间
     */
    private Date outStoreTime;

    /**
     * 用于查询
     */
    private Integer belongId;

    /**
     * 用于区分是否分成
     */
    private Integer divideInto;

    /**
     * 0未删 1 删除
     */
    private Integer deleted;

    /**
     * 销售渠道
     */
    private Integer clcId;

    /**
     * 用于表示 是否从老财务 变更过来
     */
    private Integer oldToNew;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}