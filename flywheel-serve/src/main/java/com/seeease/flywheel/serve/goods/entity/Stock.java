package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.goods.enums.StockStatusEnum;
import com.seeease.flywheel.serve.goods.enums.StockUndersellingEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存
 * @TableName stock
 */
@TableName(value ="stock")
@Data
@Accessors(chain = true)
public class Stock extends BaseDomain implements TransitionStateEntity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 全链路id
     */
    private String trackId;

    /**
     * 采购商品
     */
    private Integer goodsId;
    private Integer whetherProtect;

    /**
     * 商品编号：XYW+8位阿拉伯数字
     */
    private String wno;

    /**
     * 商品状态
     */
    @TransitionState
    private StockStatusEnum stockStatus;

    /**
     * 图片, 英文逗号分隔
     */
    private String imgs;

    /**
     * 成色 1。N级/全新、2.S级/99新未使用、3.SA级/98新未使用、4.A级/95新、5.AB级/9新、6.8新及以下
     */
    private String finess;

    /**
     * (不要为零ok？)总部采购价格 
     */
    private BigDecimal purchasePrice;

    /**
     * 维修成价
     */
    private BigDecimal fixPrice;

    /**
     * 
     */
    private BigDecimal addPrice;

    /**
     * tob价
     */
    private BigDecimal tobPrice;

    /**
     * toc价
     */
    private BigDecimal tocPrice;

    /**
     * 吊牌价
     */
    private BigDecimal tagPrice;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 门店采购价格
     */
    private BigDecimal storePrice;

    /**
     * 腕周1-99
     */
    private String week;

    /**
     * 表扣材质
     */
    private String claspMaterial;

    /**
     * 维修周期
     */
    private String fixDay;

    /**
     * 表扣号
     */
    private String clasp;

    /**
     * 表盖号
     */
    private String tableCover;

    /**
     * 表壳号
     */
    private String caseNo;

    /**
     * 表壳序号
     */
    private String caseNumber;

    /**
     * 表带号
     */
    private String strap;

    /**
     * 表带材质
     */
    private String strapMaterial;

    /**
     * 机芯序号
     */
    private String movementSequenceNumber;

    /**
     * 摆轮
     */
    private String balanceWheel;

    /**
     * 震频
     */
    private String vibrationFrequency;

    /**
     * 外观宝石镶嵌
     */
    private String egs;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 备注
     */
    private String remark;

    /**
     * 客户购入渠道 1-国内商场购买 2-海外代购 3-二手购买 4-亲友赠送  5-其他。（目前只有抖音回收/寄卖业务上才需要这个字段）
     */
    private Integer customerPurchaseWay;

    /**
     * 库存来源
     */
    private Integer stockSrc;

    /**
     * 库存异常
     */
    private String unusualDesc;

    /**
     * 库存异常图片
     */
    private String unusualImgs;

    /**
     * 表身号
     */
    private String sn;

    /**
     * purchase_subject id 结算主体 买断归属
     */
    private Integer belongId;

    /**
     * 来源主体id
     */
    private Integer sourceSubjectId;

    /**
     * 
     */
    private String belongName;

    /**
     * store_management 所在地 ID 急售商品所在地
     */
    private Integer locationId;

    /**
     * 
     */
    private String locationName;

    /**
     * 入库时间
     */
    private Date rkTime;

    /**
     * 
     */
    private Date ckTime;

    /**
     * cutomer  id
     */
    private Integer ccId;

    /**
     * 
     */
    private String company;

    /**
     * 型号
     */
    private String goodsModel;

    /**
     * 
     */
    private String supplierName;

    /**
     * 
     */
    private Integer storageLocation;

    /**
     * 
     */
    private Integer storageAge;

    /**
     * 
     */
    private Integer totalStorageAge;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date syncUpdateTime;

    /**
     * 
     */
    private Date syncCreateTime;

    /**
     * 分级
     */
    private String level;

    /**
     * 表节
     */
    private String watchSection;

    /**
     * 老系统迁移总部已售商品标示
     */
    private Integer temp;

    /**
     * 
     */
    private String weixiu;

    /**
     * 
     */
    private String date;

    /**
     * 老系统库存来源
     */
    private Integer jxcStockSrc;

    /**
     * 采购员Id
     */
    private Integer purchaserId;

    /**
     * 废弃门店入库的概念，看作经营权移交时间（平调，总部调拨，销售总部商品）,用于门店库龄计算
     */
    private Date storeRkTime;

    /**
     * 
     */
    private String warrantyDate;

    /**
     * 
     */
    private Integer isBox;

    /**
     * 
     */
    private Integer isCard;

    /**
     * 
     */
    private Integer isWarranty;

    /**
     * 
     */
    private Integer isInvoice;

    /**
     * 
     */
    private Integer isInstruction;

    /**
     * 是否允许低价销售
     */
    private StockUndersellingEnum isUnderselling;

    /**
     * 商品当前所在新
     */
    private Integer locationIdOld;

    /**
     * 寄售价
     */
    private BigDecimal consignmentPrice;

    /**
     * 商品当前所属新
     */
    private Integer belongIdOld;

    /**
     * 商品当前所在仓
     */
    private Integer storeId;

    /**
     * 商品采入仓
     */
    private Integer sourceStoreId;

    /**
     * 经营权（门店ID）
     */
    private Integer rightOfManagement;

    /**
     * 需求门店id
     */
    private Integer demandId;

    /**
     * 预估维修价格
     */
    private BigDecimal forecastFixPrice;

    /**
     * 回购价
     */
    private BigDecimal buyBackPrice;

    /**
     * 是否转回收默认0  已转回收1
     */
    private Integer isRecycling;

    /**
     * 销售优先级
     */
    private Integer salesPriority;

    /**
     * 锁门店表
     */
    private Integer lockDemand;

    /**
     * 表径
     */
    private String watchSizeRecycling;

    /**
     * 是否有瑕疵
     */
    private Integer defectOrNot;

    /**
     * 瑕疵说明
     */
    private String defectDescription;

    /**
     * 占用配资 0 是 1 否
     */
    private Integer useConfig;

    /**
     * 限量序号
     */
    private String limitedCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;

    /**
     *  物鱼供货价
     */
    private BigDecimal wuyuPrice;

    /**
     *  兜底价
     */
    private BigDecimal wuyuBuyBackPrice;

    /**
     * 最新结算价
     */
    private BigDecimal newSettlePrice;
}