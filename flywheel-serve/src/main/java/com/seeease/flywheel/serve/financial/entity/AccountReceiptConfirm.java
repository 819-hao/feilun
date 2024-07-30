package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 确认收款
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "account_receipt_confirm", autoResultMap = true)
public class AccountReceiptConfirm extends BaseDomain {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 单号---确认收款单号
     */
    private String serialNo;

    /**
     * 关联单号---关联销售单单号
     */
    private String originSerialNo;
    /**
     * 上传图片
     */
    private String batchPictureUrl;
    /**
     * 驳回原因
     */
    private String rejectionCause;

    /**
     * 状态
     * AccountReceiptConfirmStatusEnum
     * ---小程序完成新建打款单，则自动已核销
     */
    private Integer status;
    private Integer statementCompanyId;

    /**
     * 门店
     */
    private Integer shopId;

    /**
     * 总数
     * 默认1
     */
    private Integer totalNumber;

    /**
     * 收款类型:客户充值，消费收款
     * ---CollectionTypeEnum
     */
    private Integer collectionType;

    /**
     * 收款性质
     * 0寄售保证金 1客户余额 2正常销售
     * CollectionNatureEnum
     */
    private Integer collectionNature;
    /**
     * 寄售保证金
     */
    private BigDecimal consignmentMargin;
    /**
     * 正常余额
     */
    private BigDecimal accountBalance;
    /**
     * 订单分类
     * 采购退货、销售
     * OriginTypeEnum
     */
    private Integer originType;

    /**
     * 业务方式
     * FinancialSalesMethodEnum
     * 同行采购-备货---7、订金----6、批量----8
     * 个人销售-正常---1、订金----2、赠送---3
     * 同行销售-正常---1、寄售----4
     * 个人回收-仅回收----9
     */
    private Integer salesMethod;

    /**
     * 订单种类
     * ---应收应付列表的订单种类字段，确认打款单没有用到
     */
    private Integer type;

    /**
     * 订单类型
     * 1-同行采购、7-个人销售、8-同行销售、3-个人回收
     * ---FinancialClassificationEnum
     */
    private Integer classification;

    /**
     * 应收账款
     */
    private BigDecimal receivableAmount;

    /**
     * 待绑定金额
     */
    private BigDecimal waitBindingAmount;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 打款人
     */
    private String payer;

    /**
     * 联系人id
     */
    private Integer customerContractId;

    /**
     * 联系人名称
     */
    private String customerContractName;

    /**
     * 审核说明
     */
    private String auditDescription;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 审核时间
     */
    private Date auditTime;
}
