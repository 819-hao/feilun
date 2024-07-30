
package com.seeease.flywheel.serve.goods.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.flywheel.serve.goods.enums.SeriesTypeEnum;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 型号话术
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="bill_model_live_script")
@Data
public class ModelLiveScript extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer goodsWatchId;

    private String liveScript;
}