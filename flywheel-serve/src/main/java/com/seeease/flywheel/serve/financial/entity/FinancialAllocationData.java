package com.seeease.flywheel.serve.financial.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 * 财务金蝶数据配置
 * @TableName financial_allocation_data
 */
@TableName(value = "financial_allocation_data", autoResultMap = true)
@Data
public class FinancialAllocationData extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer shopId;

    /**
     * 
     */
    private String shopName;

    /**
     * 
     */
    private Integer subjectId;

    /**
     * 
     */
    private Integer douyinShopId;

    /**
     * 
     */
    private String douyinName;

    /**
     * 金蝶清算组织
     */
    private String clearingOrganization;

    /**
     * 费用承担部门
     */
    private String expenseBearingDepartment;

    /**
     * 是否抖音
     */
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}