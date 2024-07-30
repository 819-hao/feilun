package com.seeease.flywheel.serve.allocate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.allocate.enums.AllocateTaskStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 品牌调拨任务
 *
 * @TableName bill_allocate_task
 */
@TableName(value = "bill_allocate_task")
@Data
public class BillAllocateTask extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 调拨单号
     */
    private String allocateNo;

    /**
     * 调拨单id
     */
    private Integer allocateId;

    /**
     * 任务状态
     */
    private AllocateTaskStateEnum taskState;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 调出方
     */
    private Integer fromId;

    /**
     * 调入方
     */
    private Integer toId;

    /**
     * 调出仓库
     */
    private Integer fromStoreId;

    /**
     * 调入仓库
     */
    private Integer toStoreId;

    /**
     * 取消原因
     */
    private String cancelReason;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}