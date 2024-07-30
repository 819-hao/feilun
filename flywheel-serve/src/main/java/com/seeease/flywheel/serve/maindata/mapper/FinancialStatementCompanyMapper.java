package com.seeease.flywheel.serve.maindata.mapper;

import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author edy
 * @description 针对表【financial_statement_company】的数据库操作Mapper
 * @createDate 2023-11-14 15:32:56
 * @Entity com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany
 */
public interface FinancialStatementCompanyMapper extends BaseMapper<FinancialStatementCompany> {

    List<FinancialStatementCompany> queryCompanyName(@Param("subject") String sjzSubject);
}




