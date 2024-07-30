package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 附件库存导入记录
 *
 * @TableName ext_attachment_stock_record
 */
@TableName(value = "ext_attachment_stock_record")
@Data
public class ExtAttachmentStockRecord extends BaseDomain implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 采购单号
     */
    private String purchaseSerialNo;

    /**
     * 采购id
     */
    private Integer purchaseId;

    /**
     * 客户id
     */
    private Integer customerId;

    /**
     * 客户联系人
     */
    private Integer customerContactId;

    /**
     * 导入数量
     */
    private Integer countNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}