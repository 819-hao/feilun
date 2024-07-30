package com.seeease.flywheel.serve.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

import java.io.Serializable;

/**
 * 经营企业映射
 *
 * @TableName shop_company_mapping
 */
@TableName(value = "shop_company_mapping")
@Data
public class ShopCompanyMapping extends BaseDomain implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 经营类别
     */
    private String shopGroup;

    /**
     * 经营名称
     */
    private String shopName;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 部门
     */
    private String department;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}