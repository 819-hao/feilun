package com.seeease.flywheel.serve.fix.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.fix.convert.IntegerMapperTypeHandler;
import com.seeease.flywheel.serve.fix.convert.StringMapperTypeHandler;
import com.seeease.flywheel.serve.fix.enums.FixStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 维修单
 *
 * @TableName log_fix_opt
 */
@TableName(value = "log_fix_opt", autoResultMap = true)
@Data
public class LogFixOpt extends BaseDomain implements TransitionStateEntity, Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 单号
     */
    private String serialNo;

    /**
     * 仓库预作业单号
     */
    private String storeWorkSerialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 维修状态
     */
    private FixStateEnum fixState;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人id
     */
    private Integer customerContactId;

    /**
     * 维修金额
     */
    private BigDecimal fixMoney;

    /**
     * 维修天数
     */
    private Integer fixDay;

    /**
     * 维修次数
     */
    private Integer fixTimes;

    /**
     * 随表附件
     */
    private String withAttachment;

    /**
     * 图片
     */
    @TableField(typeHandler = StringMapperTypeHandler.class)
    private List<String> imgList;

    /**
     * 维修内容
     */
    /**
     * 维修内容
     */
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<FixProjectMapper> content;

    /**
     * 定金
     */
    private BigDecimal earnestMoney;

    /**
     * 送外维修原因
     */
    private String sendOutReason;

    /**
     * 送外维修公司名称
     */
    private String sendOutCompany;

    /**
     * 送外维修联系人
     */
    private String sendOutContact;

    /**
     * 维修类型-内部-送外
     */
    private Integer fixType;

    /**
     * 0不合格1合格
     */
    private Integer testResult;

    /**
     * 维修来源 1采购2其他
     */
    private BusinessBillTypeEnum fixSource;

    /**
     * 是否返修 -1全部 1是 2是不是
     */
    private Integer repairFlag;

    /**
     * 配件金款
     */
    private BigDecimal partsMoney;

    /**
     * 维修建议
     */
    private String fixAdvise;

    /**
     * 是否有瑕疵
     */
    private Integer defectOrNot;

    /**
     * 瑕疵说明
     */
    private String defectDescription;

    /**
     * 特殊加急
     */
    private Integer specialExpediting;

    /**
     * 流转等级
     */
    private Integer flowGrade;

    /**
     * 维修师id
     */
    private Integer maintenanceMasterId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 埋点时间
     */
    private Date taskArriveTime;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 品牌名
     */
    private String brandName;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 父级维修单id
     */
    private Integer parentFixId;

    /**
     * 维修站点
     */
    private Integer fixSiteId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户联系电话
     */
    private String customerPhone;

    /**
     * 客户联系地址
     */
    private String customerAddress;

    /**
     * 配件结算总价
     */
    private BigDecimal attachmentCostPrice;

    /**
     * 订单类型 0 系统自建 1 客户自建
     */
    private Integer orderType;

    /**
     * 新建维修单图片(维修结果)
     */
    @TableField(typeHandler = StringMapperTypeHandler.class)
    private List<String> newImgList;

    /**
     * 标记维修审核类型-0内部-1内部送外-2 外部送外
     */
    private Integer tagType;

    /**
     * 发货快递单号
     */
    private String deliverExpressNo;

    /**
     * 收货快递单号
     */
    private String deliveryExpressNo;

    /**
     * 维修结果项
     */
    @TableField(typeHandler = IntegerMapperTypeHandler.class)
    private List<Integer> resultContent;

    /**
     * 维修结果维修单图片(维修结果)
     */
    @TableField(typeHandler = StringMapperTypeHandler.class)
    private List<String> resultImgList;

    /**
     * 表带类型
     */
    private String strapMaterial;

    /**
     * 表节数
     */
    private String watchSection;

    /**
     * 客户诉求
     */
    private String customerDemand;

    /**
     * 完成类型0 取消 1 维修完成
     */
    private Integer finishType;

    /**
     * 建单门店id（所属门店）
     */
    private Integer storeId;

    /**
     * 来源门店id
     */
    private Integer parentStoreId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}