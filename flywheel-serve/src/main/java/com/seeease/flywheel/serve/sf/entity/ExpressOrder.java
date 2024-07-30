package com.seeease.flywheel.serve.sf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.sf.enums.ExpressOrderSourceEnum;
import com.seeease.flywheel.serve.sf.enums.ExpressOrderStateEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 物流单
 *
 * @TableName express_order
 */
@TableName(value = "express_order")
@Data
public class ExpressOrder extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 业务单号
     */
    private String serialNo;

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
    private ExpressOrderStateEnum expressState;

    /**
     * 下单来源：1-销售
     */
    private ExpressOrderSourceEnum expressSource;

    /**
     * 异常信息
     */
    private String errorMsg;
    private String requestId;
    private String sonSerialNo;

    /**
     * 门店id
     */
    private Integer storeId;

    private Integer expressChannel;

    private Long douYinShopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}