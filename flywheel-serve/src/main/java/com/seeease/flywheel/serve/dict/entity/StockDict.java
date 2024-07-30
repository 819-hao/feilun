package com.seeease.flywheel.serve.dict.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName stock_dict
 */
@TableName(value ="stock_dict")
@Data
public class StockDict implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Object id;

    /**
     * 表的id
     */
    private Integer stockId;

    /**
     * 字典的id
     */
    private Long dictId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}