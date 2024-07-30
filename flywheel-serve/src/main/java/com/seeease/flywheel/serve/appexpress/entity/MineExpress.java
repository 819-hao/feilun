package com.seeease.flywheel.serve.appexpress.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import com.seeease.seeeaseframework.mybatis.type.JsonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 金蝶飞轮费用映射
 * @author dmmasxnmf
 * @TableName cost_jd_fl_mapping
 */
@TableName(value ="mine_express",autoResultMap = true)
@Data
public class MineExpress extends BaseDomain implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String code;
    private String express;
    private Integer type;
    private String orderImage;
    private String orderVideo;
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> goodsImages;
}