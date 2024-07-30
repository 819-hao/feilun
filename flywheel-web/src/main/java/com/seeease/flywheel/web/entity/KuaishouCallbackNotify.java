package com.seeease.flywheel.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 快手消息通知
 *
 * @TableName kuaishou_callback_notify
 */
@TableName(value = "kuaishou_callback_notify")
@Data
public class KuaishouCallbackNotify extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 快手门店id
     */
    private Long kuaiShouSellerId;
    /**
     * 快手订单id
     */
    private String kuaiShouOrderId;

    /**
     * 消息唯一id
     */
    private String eventId;
    /**
     * 业务消息内容唯一id
     */
    private String msgId;
    /**
     * 业务id如订单id、退款单id、商品id
     */
    private Long bizId;
    /**
     * 授权用户id
     */
    private Long userId;
    /**
     * 授权用户openId
     */
    private String openId;
    /**
     * 应用id
     */
    private String appKey;
    /**
     * 消息标示
     */
    private String event;
    /**
     * 状态 0未知 1发送中 2发送成功 3发送失败
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 消息内容，业务内容Json串，详见消息文档参数
     */
    private String info;

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