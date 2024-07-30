package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * @TableName store_relationship_subject
 */
@TableName(value = "store_relationship_subject")
@Data
public class StoreRelationshipSubject extends BaseDomain {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer subjectId;

    /**
     *
     */
    private Integer storeManagementId;

    /**
     *
     */
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}