package com.seeease.flywheel.serve.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.purchase.enums.PurchaseModeEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleOrderTypeEnum;
import com.seeease.flywheel.serve.recycle.enums.RecycleStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 回收和回购实体
 *
 * @author dmmasxnmf
 * @Auther Gilbert
 * @Date 2023/8/30 20:37
 */
@TableName(value = "mall_recycling_order", autoResultMap = true)
@Data
@Accessors(chain = true)
public class MallRecyclingOrder extends BaseDomain implements TransitionStateEntity, Serializable {


    /**
     * 回收回购表
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 批次号
     */
    private String serial;

    /**
     * 原销售单号
     */
    private String saleSerialNo;

    /**
     * 原销售单号商品id
     */
    private Integer stockId;

    /**
     * 供应商表id
     */
    private Integer customerId;

    /**
     * 联系人
     */
    private Integer customerContactId;

    /**
     * 客户经理
     */
    private Integer employeeId;

    /**
     * 品牌id（要采购）
     */
    private Integer brandId;

    /**
     * 采购id
     */
    private Integer purchaseId;

    /**
     * 销售id
     */
    private Integer saleId;

    /**
     * 检测id
     */
    private Integer detectionId;

    /**
     * 年份
     */
    private String year;

    /**
     * 估价价格一次任意文本
     */
    private String valuationPrice;

    /**
     * 第一次估价备注
     */
    private String valuationRemark;

    /**
     * 第一次报价人
     */
    private Integer valuationPriceId;

    /**
     * 估价图片
     */
    private String valuationImage;

    /**
     * 估价价格二次
     */
    private String valuationPriceTwo;

    /**
     * 估价第二次备注
     */
    private String valuationPriceTwoRemark;

    /**
     * 第二次报价人
     */
    private Integer valuationPriceTwoId;

    /**
     * 估价时间
     */
    private Date valuationTime;

    /**
     * 回收协议
     */
    private String agreement;


    /**
     * 状态
     */

    /**
     * 状态
     *
     * @see RecycleStateEnum
     */
    @TransitionState
    private RecycleStateEnum state;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商城图片
     */
    private String shopImage;

    /**
     * 需求门店id
     */
    private Integer demandId;

    /**
     * 估价单主键
     */
    private String assessId;

    /**
     * 类型用来区分来源
     */
    private PurchaseModeEnum type;

    /**
     * 区分大类回购还是回购
     */
    private RecycleOrderTypeEnum recycleType;

    /**
     * 三方关联单号
     */
    private String bizOrderCode;

    /**
     * 物流单号
     */
    private String expressNumber;

    /**
     * 退货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 差额
     */
    private BigDecimal balance;

    /**
     * 符号 -1 待打款 0 平账 1 待收款
     */
    private Integer symbol;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 表节
     */
    private String watchSection;

    /**
     * 表径
     */
    private String watchSize;

    /**
     * 银行名称
     */
    private String accountName;

    /**
     * 银行卡号
     */
    private String bankAccount;

    /**
     * 开户银行地址
     */
    private String bank;

    /**
     * 银行客户名称
     */
    private String bankCustomerName;

    /**
     * 证件正面图片（个人采购）
     */
    private String frontIdentityCard;

    /**
     * 证件反面图片（个人采购）
     */
    private String reverseIdentityCard;

    /**
     * 回收聊天记录（个人采购）
     */
    private String recoveryPricingRecord;

    /**
     * 转让协议
     */
    private String agreementTransfer;

    /**
     * 采购表身号
     */
    private String stockSn;

    /**
     * 成色
     */
    private String finess;

    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Integer> attachmentList;

    /**
     * 保卡日期
     */
    private String warrantyDate;

    /**
     * 0 无 1 有 2空白保卡
     */
    private Integer isCard;

    /**
     * 上传图片
     */
    private String valuationImageTwo;

    /**
     * 二次估价时间
     */
    private Date valuationTimeTwo;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}
