package com.seeease.flywheel.serve.goods.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName buy_back_policy_activity
 */
@TableName(value ="buy_back_policy_activity")
@Data
public class BuyBackPolicyActivity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer stockId;

    /**
     * 
     */
    private String stockSn;

    /**
     * 
     */
    private Date effectiveStartTime;

    /**
     * 
     */
    private Date effectiveEndTime;

    /**
     * 0:代表是没有回购政策 1:代表固定回购政策
     */
    private Integer activityType;

    /**
     * 只有类型为1 的这个数据才不为null
     */
    private Integer bbpId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}