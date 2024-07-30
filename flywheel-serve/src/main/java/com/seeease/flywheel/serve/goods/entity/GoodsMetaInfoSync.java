package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 商品元数据同步
 * @TableName goods_meta_info
 */
@TableName(value ="goods_meta_info_sync")
@Data
public class GoodsMetaInfoSync implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 库存id
     */
    private Integer stockId;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 二手表商场订单编号
     */
    private String orderNo;

    /**
     * toc销售价
     */
    private BigDecimal tocPrice;

    /**
     * 商品状态
     */
    private Integer stockState;

    /**
     * 客户信息
     */
    private String customerInfo;

    /**
     * 是否全新表
     */
    private Integer brandNew;

    /**
     * 全新表配置
     */
    private String brandNewConfig;

    /**
     * 属性是否发生变动
     */
    private Integer propertyChange;

    /**
     * 消息发送时间
     */
    private Date noticeTime;

    /**
     * 拉取状态
     */
    private Integer latestPullState;

    /**
     * 最后拉取数据信息
     */
    private String latestPullData;

    /**
     * 最后拉取数据时间
     */
    private Date latestPullTime;

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 创建人id
     */
    private Integer createdId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改人id
     */
    private Integer updatedId;

    /**
     * 修改人
     */
    private String updatedBy;

    /**
     * 修改时间
     */
    private Date updatedTime;

    /**
     * 删除标识
     */
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}