package com.seeease.flywheel.serve.financial.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seeease.flywheel.financial.request.FinancialInvoiceReverseQueryByConditionRequest;
import com.seeease.flywheel.financial.result.FinancialInvoiceReverseQueryByConditionResult;
import com.seeease.flywheel.serve.financial.entity.FinancialInvoiceReverse;
import com.seeease.flywheel.serve.financial.service.FinancialInvoiceReverseService;
import com.seeease.flywheel.serve.financial.mapper.FinancialInvoiceReverseMapper;
import org.springframework.stereotype.Service;

/**
* @author edy
* @description 针对表【financial_invoice_reverse】的数据库操作Service实现
* @createDate 2023-11-30 09:49:04
*/
@Service
public class FinancialInvoiceReverseServiceImpl extends ServiceImpl<FinancialInvoiceReverseMapper, FinancialInvoiceReverse>
    implements FinancialInvoiceReverseService{

    @Override
    public Page<FinancialInvoiceReverseQueryByConditionResult> queryByCondition(FinancialInvoiceReverseQueryByConditionRequest request) {
        return this.baseMapper.queryByCondition(new Page(request.getPage(), request.getLimit()), request);
    }
}




