package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 抖音消息通知
 *
 * @TableName douyin_callback_notify
 */
@TableName(value = "douyin_callback_notify")
@Data
public class DouYinCallbackNotify extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 抖音门店id
     */
    private Long douYinShopId;
    /**
     * 抖音订单id
     */
    private String douYinOrderId;
    /**
     * 消息记录ID
     */
    private String msgId;

    /**
     * 消息种类
     */
    private String tag;

    /**
     * 消息体
     */
    private String data;

    /**
     * 处理状态
     */
    private Integer state;

    /**
     * 异常原因
     */
    private String errorReason;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}