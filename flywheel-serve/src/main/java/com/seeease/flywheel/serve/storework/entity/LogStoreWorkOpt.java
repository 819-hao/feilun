package com.seeease.flywheel.serve.storework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.base.BusinessBillTypeEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkCommoditySituationEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkOptTypeEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.StoreWorkTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.domain.WhetherEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.util.Date;

/**
 * 仓库作业操作记录
 *
 * @TableName log_store_work_opt
 */
@TableName(value = "log_store_work_opt")
@Data
public class LogStoreWorkOpt extends BaseDomain implements TransitionStateEntity {
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
     * 操作类型
     */
    private StoreWorkOptTypeEnum optType;

    /**
     * 工作类型：1-出库，2-入库
     */
    private StoreWorkTypeEnum workType;

    /**
     * 来源
     */
    private BusinessBillTypeEnum workSource;

    /**
     * 预作业单号
     */
    private String serialNo;

    /**
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 之前作业单状态
     */
    private StoreWorkStateEnum fromWorkState;

    /**
     * 作业单状态
     */
    private StoreWorkStateEnum toWorkState;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 物流单号
     */
    private String expressNumber;

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
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}