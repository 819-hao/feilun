package com.seeease.flywheel.serve.storework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.storework.enums.WmsWorkCollectWorkStateEnum;
import com.seeease.flywheel.serve.storework.enums.WmsWorkPrintExpressState;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.transitionstate.ITransitionStateEnum;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionState;
import com.seeease.seeeaseframework.mybatis.transitionstate.TransitionStateEntity;
import lombok.Data;

/**
 * 发货作业集单表
 *
 * @TableName wms_work_collect
 */
@TableName(value = "wms_work_collect")
@Data
public class WmsWorkCollect extends BaseDomain implements TransitionStateEntity {
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
     * 源头单据单号
     */
    private String originSerialNo;

    /**
     * 状态：1-待打单，2-待发货，3-已发货，4-已取消,5-取消已回收物流单
     */
    @TransitionState
    private WmsWorkCollectWorkStateEnum workState;

    /**
     * 集单打单状态：1-系统打单，2-人工录入快递单
     */
    private WmsWorkPrintExpressState printExpressState;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private ITransitionStateEnum transitionStateEnum;
}