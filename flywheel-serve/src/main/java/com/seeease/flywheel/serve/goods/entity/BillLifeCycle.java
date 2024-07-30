package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 生命周期
 * @TableName bill_life_cycle
 */
@TableName(value ="bill_life_cycle")
@Data
public class BillLifeCycle extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品编码
     */
    private String wno;

    /**
     * 表的id
     */
    private Integer stockId;

    /**
     * 关联单据
     */
    private String originSerialNo;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 当前门店
     */
    private Integer storeId;

    /**
     * 操作时间，时间戳
     */
    private Long operationTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}