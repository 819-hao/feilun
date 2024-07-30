package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import java.io.Serializable;
import lombok.Data;

/**
 * 库存管理信息
 * @TableName stock_manage_info
 */
@TableName(value ="stock_manage_info")
@Data
public class StockManageInfo extends BaseDomain implements Serializable {
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
     * 表身号
     */
    private String stockSn;

    /**
     * 盒子编号
     */
    private String boxNumber;

    /**
     * 库位
     */
    private Integer storageId;

    /**
     * 库位大区
     */
    private String storageRegion;

    /**
     * 库位子区
     */
    private String storageSubsegment;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}