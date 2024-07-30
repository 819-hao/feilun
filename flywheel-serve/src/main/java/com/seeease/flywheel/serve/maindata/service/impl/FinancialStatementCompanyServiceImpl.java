package com.seeease.flywheel.serve.maindata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.serve.maindata.entity.FinancialStatementCompany;
import com.seeease.flywheel.serve.maindata.service.FinancialStatementCompanyService;
import com.seeease.flywheel.serve.maindata.mapper.FinancialStatementCompanyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author edy
* @description 针对表【financial_statement_company】的数据库操作Service实现
* @createDate 2023-11-14 15:32:56
*/
@Service
public class FinancialStatementCompanyServiceImpl extends ServiceImpl<FinancialStatementCompanyMapper, FinancialStatementCompany>
    implements FinancialStatementCompanyService{

    @Override
    public List<FinancialStatementCompany> queryCompanyName(String sjzSubject) {
        return this.baseMapper.queryCompanyName(sjzSubject);
    }
}




