package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 附件消耗关联配件
 * @TableName attachment_consume_log
 */
@TableName(value ="attachment_consume_log")
@Data
public class AttachmentConsumeLog extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 维修单ID
     */
    private String originOrderSerialNo;

    /**
     * 配件id
     */
    private Integer stockId;

    /**
     * 表身号
     */
    private String stockSn;

    /**
     * 成本价格
     */
    private BigDecimal costPrice;

    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}