package com.seeease.flywheel.serve.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 金蝶飞轮费用映射
 * @author dmmasxnmf
 * @TableName cost_jd_fl_mapping
 */
@TableName(value ="cost_jd_fl_mapping")
@Data
public class CostJdFlMapping extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 飞轮费用归类
     */
    private String flGroup;

    /**
     * 飞轮费用分类
     */
    private String flType;

    /**
     * 金蝶科目编码
     */
    private String jdGroup;

    /**
     * 金蝶核算维度类型
     */
    private String jdType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}