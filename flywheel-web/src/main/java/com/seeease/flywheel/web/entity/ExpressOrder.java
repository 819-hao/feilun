package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.web.common.express.channel.ExpressChannelTypeEnum;
import com.seeease.flywheel.web.entity.enums.ExpressOrderStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

import java.util.Date;

/**
 * 物流单
 *
 * @TableName express_order
 */
@TableName(value = "express_order")
@Data
public class ExpressOrder extends BaseDomain implements TransitionStateEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 渠道:1 顺丰 2 抖店
     */
    private ExpressChannelTypeEnum expressChannel;

    /**
     * 业务单号
     */
    private String serialNo;

    /**
     * 子单号
     */
    private String sonSerialNo;

    /**
     * 物流单号
     */
    private String expressNo;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 物流状态:1 初始化 2 下单成功 3 下单失败
     */
    @TransitionState
    private ExpressOrderStateEnum expressState;

    /**
     * 下单来源：1-销售
     */
    private Integer expressSource;

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 问题定位id
     */
    private String requestId;

    /**
     * 门店id
     */
    private Integer storeId;

    /**
     * 抖音门店id
     */
    private Long douYinShopId;

    /**
     * 锁定时间
     */
    private Long lockTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}