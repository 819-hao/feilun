package com.seeease.flywheel.serve.goods.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 回购政策商品表
 *
 * @TableName buy_back_policy_stock
 */
@TableName(value = "buy_back_policy_stock")
@Data
public class BuyBackPolicyStock implements Serializable {
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
     * 保卡时间
     */
    private Date warrantyDate;

    /**
     *
     */
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}