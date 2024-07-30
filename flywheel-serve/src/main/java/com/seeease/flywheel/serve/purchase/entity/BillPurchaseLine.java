package com.seeease.flywheel.serve.purchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.purchase.enums.PurchaseLineStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购单_表
 *
 * @TableName bill_purchase_line
 */
@TableName(value = "bill_purchase_line", autoResultMap = true)
@Data
public class BillPurchaseLine extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购单id
     */
    private Integer purchaseId;

    /**
     * 型号id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 采购表身号
     */
    private String stockSn;
    /**
     * 老的sn
     */
    private String oldStockSn;
    private String oldModel;

    /**
     * 成色
     */
    private String finess;

    /**
     * 销售等级
     */
    private Integer salesPriority;

    /**
     * 附件列表
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Integer> attachmentList;

    private String oldAttachment;

    /**
     * 采购价-不转换（同行寄售，同行采购批量，同行采购订金，同行采购备货）
     * 采购价-转换（个人寄售，个人回购回收，个人回购置换，个人回收，个人回收置换）
     */
    private BigDecimal purchasePrice;
    /**
     * 旧的采购价 只有进行补差之后 才会有
     */
    private BigDecimal oldPurchasePrice;

    /**
     * 采购单行状态
     */
//    @TransitionState
    private PurchaseLineStateEnum purchaseLineState;

    /**
     * 关联表id（个人回购）
     */
    private Integer originStockId;

    /**
     * 关联退货单号（同行采购，同行寄售）
     */
    private String originPurchaseReturnSerialNo;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 商品级别
     */
    private String goodsLevel;

    /**
     * 表带类型（个人回收，个人置换，个人回购）
     */
    private String strapMaterial;

    /**
     * 表节（个人回收，个人置换，个人回购）
     */
    private String watchSection;

    /**
     * 保卡日期
     */
    private String warrantyDate;

    /**
     * 0 无 1 有 2空白保卡
     */
    private Integer isCard;

    /**
     * 回收价（回收）
     */
    private BigDecimal recyclePrice;

    /**
     * 本次销售价（个人回购，个人置换）
     */
    private BigDecimal salePrice;

    /**
     * 寄售协议价（个人寄售）
     */
    private BigDecimal dealPrice;

    /**
     * 实际维修费（个人寄售）
     */
    private BigDecimal fixPrice;

    /**
     * 预计维修费（个人寄售，个人回购）
     */
    private BigDecimal planFixPrice;

//    /**
//     * 旧预计维修费（个人寄售，个人回购）
//     */
//    private BigDecimal oldPlanFixPrice;
    /**
     * 回购服务费（个人回购）
     */
    private BigDecimal recycleServePrice;

    /**
     * 实际回购价（回购）
     */
    private BigDecimal buyBackPrice;

    /**
     * 参考回购价（回购）
     */
    private BigDecimal referenceBuyBackPrice;

    /**
     * 寄售价（个人回购，寄售到门店价格）
     */
    private BigDecimal consignmentPrice;
    /**
     * 寄售成交价（回购）
     */
    private BigDecimal clinchPrice;

    /**
     * 表带更换费（回购）
     */
    private BigDecimal watchbandReplacePrice;

//    /**
//     *  旧表带更换费（回购）
//     */
//    private BigDecimal oldWatchbandReplacePrice;

    /**
     * 关联申请单id
     */
    private Integer originApplyPurchaseId;

    /**
     * 模块
     */
    private Integer module;

    /**
     * 状态
     */
    private Integer state;
    /**
     * 是否结算
     */
    private WhetherEnum whetherSettle;

    /**
     * 版本,和表身号构成唯一索引
     */
    private Integer edition;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 返修原因
     */
    private String returnFixRemarks;
    /**
     *  物鱼供货价
     */
    private BigDecimal wuyuPrice;

    /**
     *  兜底价
     */
    private BigDecimal wuyuBuyBackPrice;


}