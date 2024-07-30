package com.seeease.flywheel.serve.maindata.service;

import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
* @author edy
* @description 针对表【financial_statement_company】的数据库操作Service
* @createDate 2023-11-14 15:32:56
*/
public interface FinancialStatementCompanyService extends IService<FinancialStatementCompany> {

    List<FinancialStatementCompany> queryCompanyName(String sjzSubject);
}
