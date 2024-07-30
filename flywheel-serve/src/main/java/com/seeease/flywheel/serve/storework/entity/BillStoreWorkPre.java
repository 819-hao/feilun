package com.seeease.flywheel.serve.storework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkCommoditySituationEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkReturnTypeEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.util.Date;

/**
 * 仓库预作业单
 *
 * @TableName bill_store_work_pre
 */
@TableName(value = "bill_store_work_pre")
@Data
public class BillStoreWorkPre extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 归属门店id
     */
    private Integer belongingStoreId;

    /**
     * 工作类型：1-出库，2-入库
     */
    private StoreWorkTypeEnum workType;

    /**
     * 来源
     */
    private BusinessBillTypeEnum workSource;

    /**
     * 返修类型
     */
    private StoreWorkReturnTypeEnum returnType;

    /**
     * 预作业单号
     */
    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 作业单状态
     */
    @TransitionState
    private StoreWorkStateEnum workState;

    /**
     * 配对标记
     */
    private String mateMark;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系id
     */
    private Integer customerContactId;

    /**
     * 物流单号
     */
    private String expressNumber;

    /**
     * 发货物流单号
     */
    private String deliveryExpressNumber;

    /**
     * 商品情况：0-正常，1-缺货，2-商品实物不符
     */
    private StoreWorkCommoditySituationEnum commoditySituation;

    /**
     * 任务到手时间
     */
    private Date taskArriveTime;

    /**
     * 异常标记
     */
    private WhetherEnum exceptionMark;


    /**
     * 保卡管理-是否已调拨
     */
    private Integer guaranteeCardManage;

    /**
     * 备注
     */
    private String remarks;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;

    @TableField(exist = false)
    private Integer number;

}