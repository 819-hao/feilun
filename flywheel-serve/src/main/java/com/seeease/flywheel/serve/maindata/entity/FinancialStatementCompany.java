package com.seeease.flywheel.serve.maindata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seeease.seeeaseframework.mybatis.domain.BaseDomain;
import lombok.Data;

/**
 *
 * 	find_in_set(
 * 		'15',
 * 	subject_id
 * 	)
 * @TableName financial_statement_company
 */
@TableName(value ="financial_statement_company")
@Data
public class FinancialStatementCompany extends BaseDomain {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String companyName;

    /**
     * 
     */
    private String subjectId;

}