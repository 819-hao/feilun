package com.seeease.flywheel.serve.sf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 物流单打印
 * @TableName express_order_print
 */
@TableName(value ="express_order_print")
@Data
public class ExpressOrderPrint extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 物流单id
     */
    private Integer expressOrderId;

    /**
     * 打印模版
     */
    private String printTemplate;

    /**
     * 备注说明
     */
    private String remarks;

    /**
     * 打印状态: 1 下单成功 2 下单失败
     */
    private Integer printState;

    /**
     * 打印来源：1-pc
     */
    private Integer printSource;

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 异常定位id
     */
    private String requestId;

    /**
     * 门店id
     */
    private Integer storeId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}